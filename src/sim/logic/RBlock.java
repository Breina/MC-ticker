package sim.logic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import logging.Log;
import sim.loading.Linker;
import sim.objects.WorldInstance;

/**
 * This class is an intermediate between the Simulator's high level logic and all of Block's reflection
 */
public class RBlock implements ISimulated {
	
	private Class<?> Block, EntityPlayer;
	private Method m_getBlockById, m_getIdFromBlock, m_hasTileEntity, m_onBlockActivated;
	private Field f_unlocalizedNameBlock;
	
	 // A buffer for all blocks that were once obtained
	private HashMap<Integer, Object> bufferedBlocks;
	
	public RBlock(Linker linker) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		
		bufferedBlocks = new HashMap<Integer, Object>();
		
		prepareBlock(linker);
		
		Log.i("Preparing Blocks");
	}
	
	/**
	 * Prepares all methods and fills up blockRegistry
	 */
	private void prepareBlock(Linker linker) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		
		Block = linker.getClass("Block");
		EntityPlayer = linker.getClass("EntityPlayer");
		
//		Method m_registerBlocks = linker.method("registerBlocks", Block);
//		m_registerBlocks.invoke(null, new Object[]{}); // adds blocks to blockRegistry
		
//		Class<?> FireBlock = linker.getClass("BlockFire");
//		Method m_register = FireBlock.getDeclaredMethod(Constants.FIREBLOCK_REGISTER);
//		m_register.invoke(null);
		
		f_unlocalizedNameBlock = linker.field("unlocalizedNameBlock", Block);
		
		m_getBlockById = linker.method("getBlockById", Block, int.class);
		m_getIdFromBlock = linker.method("getIdFromBlock", Block, Block);
		m_hasTileEntity = linker.method("hasTileEntity", Block);
		m_onBlockActivated = linker.method("onBlockActivated", Block, linker.getClass("World"), int.class, int.class, int.class, EntityPlayer, int.class, float.class, float.class, float.class);
	}
	
	/**
	 * Gets a Block object by its id
	 * @param id The block id
	 * @return The Block object
	 */
	public Object getBlock(byte byteId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		
		int id = compansateForJavasLackOfUnsignedBytes(byteId);
		
		Object block;
		
		if (bufferedBlocks.containsKey(id))
			
			block = bufferedBlocks.get(id);
		
		else {
			
			block = m_getBlockById.invoke(null, new Object[]{ id });
			bufferedBlocks.put(id, block);
			
			if (bufferedBlocks.size() > Byte.MAX_VALUE)
				throw new IllegalStateException("Too many buffered blocks, how is this possible? xD");
		}
		
		return block;		
	}
	
	public void onBlockActivated(Object block, WorldInstance world, int x, int y, int z, Object player, int side, float vecX, float vexY, float vecZ) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		m_onBlockActivated.invoke(block, world.getWorld(), x, y, z, player, side, vecX, vexY, vecZ);
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
	
	/**
	 * Get the block name of a block object by its field
	 * @param block The block object
	 * @return Its unlocalized name
	 */
	public String getBlockName(Object block) throws IllegalArgumentException, IllegalAccessException  {
		
		Object blockName = f_unlocalizedNameBlock.get(block);
		
		return blockName.toString();
	}
	
	public int getIdFromBlock(Object block) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		int blockId = (int) m_getIdFromBlock.invoke(null, block);
		
		return blockId;
	}

	@Override
	public Class<?> getReflClass() {
		
		return Block;
	}
}
