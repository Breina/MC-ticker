package sim.logic;

import logging.Log;
import sim.constants.Constants;
import sim.loading.Linker;
import sim.objects.WorldInstance;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is an intermediate between the Simulator's high level logic and all of Block's reflection
 */
public class RBlock {
	
	private Class<?> Block, EntityPlayer;
	private Method m_getBlockById, m_getIdFromBlock, m_hasTileEntity, m_onBlockActivated,
		m_getStateFromMeta, m_getMetaFromState, m_getBlock, m_getBlockFromName, m_getValue, m_getProperties,
		m_getNameForObject, m_onBlockEventReceived;
	private Field f_unlocalizedName, f_blockRegistry;

	private RBlockPos rBlockPos;

	// TODO fix this
	private Object propertyFacing;

	private Object blockRegistry;
	
	 // A buffer for all blocks that were once obtained
	private HashMap<Integer, Object> bufferedBlocks;
	
	public RBlock(Linker linker, RBlockPos rBlockPos) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		
		bufferedBlocks = new HashMap<>();
		this.rBlockPos = rBlockPos;
		
		prepareBlock(linker);
		
		Log.i("Preparing Blocks");
	}
	
	/**
	 * Prepares all methods and fills up blockRegistry
	 */
	private void prepareBlock(Linker linker) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		
		Block = linker.getClass("Block");
		EntityPlayer = linker.getClass("EntityPlayer");
		
		Class<?> IBlockState = linker.getClass("IBlockState");
		Class<?> PropertyDirection = linker.getClass("PropertyDirection");
		Class<?> IProperty = linker.getClass("IProperty");
		Class<?> RegistryNamespaced = linker.getClass("RegistryNamespaced");
		Class<?> World = linker.getClass("World");
		Class<?> BlockPos = linker.getClass("BlockPos");

		m_getBlockById = linker.method("getBlockById", Block, int.class);
		m_getStateFromMeta = linker.method("getStateFromMeta", Block, int.class);
		m_getIdFromBlock = linker.method("getIdFromBlock", Block, Block);
		m_getMetaFromState = linker.method("getMetaFromState", Block, IBlockState);
		m_getBlockFromName = linker.method("getBlockFromName", Block, String.class);
		m_getValue = linker.method("getValue", IBlockState, IProperty);
		m_hasTileEntity = linker.method("hasTileEntity", Block);
		m_getProperties = linker.method("getProperties", IBlockState);
		m_onBlockEventReceived = linker.method("onBlockEventReceived", Block, World, BlockPos, IBlockState, int.class, int.class);

		m_onBlockActivated = linker.method("onBlockActivated", Block, linker.getClass("World"), linker.getClass("BlockPos"),
				IBlockState, EntityPlayer, linker.getClass("EnumFacing"), float.class, float.class, float.class);

		propertyFacing = linker.method("create", PropertyDirection, String.class).invoke(null, "facing");
		m_getNameForObject = linker.method("getNameForObject", RegistryNamespaced, Object.class);

		f_blockRegistry = linker.field("blockRegistry", Block);

		// TODO can't use linker yet
		m_getBlock = IBlockState.getDeclaredMethod(Constants.IBLOCKSTATE_GETBLOCK);

		f_unlocalizedName = Block.getDeclaredField(Constants.BLOCK_UNLOCALIZEDNAME);
		f_unlocalizedName.setAccessible(true);

	}
	
	public Object getBlockFromName(String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// TODO buffer with name as well
		Object block = m_getBlockFromName.invoke(null, name);
		
		return block;
	}

	/**
	 * Get the humanly readable name of a block.
	 * DO NOT USE THIS FOR INTERNAL PURPOSES
	 * @param block The block object
	 * @return Its unlocalized name
	 */
	public String getReadableBlockName(Object block) throws IllegalArgumentException, IllegalAccessException  {

		return (String) f_unlocalizedName.get(block);
	}

	/**
	 * Gets the internally used name of a block
	 */
	public String getInternalBlockName(Object block) throws InvocationTargetException, IllegalAccessException {

		String internalName = m_getNameForObject.invoke(f_blockRegistry.get(block), block).toString();
		return internalName;
	}

	public Object getBlockById(byte byteId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		
		int id = compansateForJavasLackOfUnsignedBytes(byteId);
		
		Object block;
		
		if (bufferedBlocks.containsKey(id))
			
			block = bufferedBlocks.get(id);
		
		else {
			
			block = m_getBlockById.invoke(null, id);
			bufferedBlocks.put(id, block);
			
			if (bufferedBlocks.size() > 256)
				throw new IllegalStateException("Too many buffered blocks, how is this possible? xD");
		}
		
		return block;		
	}
	
	public Object getStateFromMeta(Object block, byte data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object blockState = m_getStateFromMeta.invoke(block, (int) data);
		
		return blockState;
	}

	// World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	public void onBlockActivated(Object blockState, WorldInstance world, int x, int y, int z, float vecX, float vexY, float vecZ) throws InvocationTargetException, IllegalAccessException, InstantiationException {

		Object block = getBlockFromState(blockState);
		Object blockPos = rBlockPos.createInstance(x, y, z);

		m_onBlockActivated.invoke(block, world.getWorld(), blockPos, blockState, world.getPlayer(), null, vecX, vexY, vecZ);
	}
	
	private int compansateForJavasLackOfUnsignedBytes(byte b) {
		
		int id = (int) b;
		
		if (id < 0)
			id += 256;
		
		return id;		
	}

	/**
	 * Seems to always return false
	 * @param block
	 * @return
	 */
	public boolean hasTileEntity(Object block) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		boolean doesIt = (boolean) m_hasTileEntity.invoke(block);
		
		return doesIt;
	}
	
	public int getIdFromBlock(Object block) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		int blockId = (int) m_getIdFromBlock.invoke(null, block);
		
		return blockId;
	}
	
	public int getMetaFromState(Object block, Object blockState) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		int metaData = (int) m_getMetaFromState.invoke(block, blockState);
		
		return metaData;
	}

	public Object getBlockFromState(Object state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object block = m_getBlock.invoke(state);
		
		return block;
	}

	public boolean onBlockEventReceived(Object block, Object world, Object blockPos, Object blockState, int eventId, int eventParameter) throws InvocationTargetException, IllegalAccessException {

		return (boolean) m_onBlockEventReceived.invoke(block, world, blockPos, blockState, eventId,  eventParameter);
	}

	public Object getPropertyFacing(Object blockState) throws InvocationTargetException, IllegalAccessException {

		System.out.println("RBlock DEBUG");

		System.out.println("Our property: " + propertyFacing.hashCode() + " " + propertyFacing);

		System.out.println("BlockState's properties");

		Map<?, ?> props = getProperties(blockState);
		Iterator<?> keys = props.keySet().iterator();
		Iterator<?> values = props.values().iterator();

		while (keys.hasNext()) {
			Object key = keys.next();
			System.out.println(key.hashCode() + " " + key + ": " + values.next());
		}
		return m_getValue.invoke(blockState, propertyFacing);
	}
	public Map getProperties(Object blockState) throws InvocationTargetException, IllegalAccessException {

		return (Map) m_getProperties.invoke(blockState);
	}
}
