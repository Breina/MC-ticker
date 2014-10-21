package sim.logic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import logging.Log;
import sim.constants.Constants;
import sim.exceptions.UnimplementedException;
import sim.loading.ClassTester;
import sim.loading.Linker;
import sim.objects.WorldInstance;

/**
 * This class is an intermediate between the Simulator's high level logic and all of Block's reflection
 */
public class RBlock {
	
	private Class<?> Block, EntityPlayer;
	private Method m_getBlockById, m_getIdFromBlock, m_hasTileEntity, m_onBlockActivated,
		m_getStateFromMeta, m_getMetaFromState, m_getBlock;
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
		
		Class<?> IBlockState = linker.getClass("IBlockState");
		
		// TODO broken 1.8
//		f_unlocalizedNameBlock = linker.field("unlocalizedNameBlock", Block);
		
		m_getBlockById = linker.method("getBlockById", Block, int.class);
		m_getStateFromMeta = linker.method("getStateFromMeta", Block, int.class);
		m_getIdFromBlock = linker.method("getIdFromBlock", Block, Block);
		m_getMetaFromState = linker.method("getMetaFromState", Block, IBlockState);
		
		m_hasTileEntity = linker.method("hasTileEntity", Block);
		
		// TODO can't use linker yet
		m_getBlock = IBlockState.getDeclaredMethod(Constants.IBLOCKSTATE_GETBLOCK);
		
		// TODO broken 1.8
//		m_onBlockActivated = linker.method("onBlockActivated", Block, linker.getClass("World"), int.class, int.class, int.class, EntityPlayer, int.class, float.class, float.class, float.class);
	}
	
	/**
	 * Gets a Block object by its id
	 * @param id The block id
	 * @return The Block object
	 */

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
	
	public void onBlockActivated(Object block, WorldInstance world, int x, int y, int z, Object player, int side, float vecX, float vexY, float vecZ) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Log.e("onBlockActivated TODO 1.8");
		
//		m_onBlockActivated.invoke(block, world.getWorld(), x, y, z, player, side, vecX, vexY, vecZ);
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
		
		Log.e("getBlockName TODO 1.8");
		return null;
		
//		Object blockName = f_unlocalizedNameBlock.get(block);
//		
//		return blockName.toString();
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
}
