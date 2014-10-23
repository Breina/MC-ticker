
package sim.logic;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import logging.Log;
import sim.loading.Linker;

/**
 * This class is an intermediate between the Simulator's high level logic and all of Chunk's reflection
 */
public class RChunk {
	
	private Class<?> Chunk, Block;
	private Constructor<?> c_chunk;
	private Method m_genHeightMap, m_addTileEntity, m_onChunkLoad;
	private RBlock rBlock;
	private RChunkPrimer rChunkPrimer;
	
	public RChunk(Linker linker, RBlock rBlock, RChunkPrimer rChunkPrimer) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		prepareChunk(linker);
		this.rBlock = rBlock;
		this.rChunkPrimer = rChunkPrimer;
		
		Log.i("Preparing Chunks");
	}
	
	/**
	 * Prepares all reflection about to happen
	 */
	private void prepareChunk(Linker linker) throws NoSuchMethodException, SecurityException {
		
		Chunk = linker.getClass("Chunk");
		Class<?> World = linker.getClass("World");
		Class<?> TileEntity = linker.getClass("TileEntity");
		Class<?> ChunkPrimer = linker.getClass("ChunkPrimer");
		
		c_chunk = Chunk.getDeclaredConstructor(World, ChunkPrimer, int.class, int.class);

		m_genHeightMap = linker.method("generateSkylightMap", Chunk);
		m_addTileEntity = linker.method("addTileEntity", Chunk, TileEntity);
		m_onChunkLoad = linker.method("onChunkLoad", Chunk);
	}
	
	public void addTileEntity(Object chunk, Object tileEntity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_addTileEntity.invoke(chunk, tileEntity );
		
	}
	
	public void onChunkLoad(Object chunk) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_onChunkLoad.invoke(chunk);
	}
	
	public Object generateEmptyChunk(Object world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Object primer = rChunkPrimer.createChunkPrimer();
		short[] data = new short[65536];
		rChunkPrimer.setData(primer, data);
		
		Object emptyChunk = createChunk(world, primer, 0, 0);
		
		return emptyChunk;
	}
	
	/**
	 * Generates a new chunk from its constructor
	 * @param world The World object where this chunk will be a part of
	 * @param blockArray An array containing all block objects (dividable by 256 (16x16))
	 * @param metaData An array of the same size containing the block data
	 * @param xPos The X chunk position
	 * @param zPos The Y chunk position
	 * @return The Chunk object
	 */
	public Object createChunk(Object world, Object chunkPrimer, int xPos, int zPos) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object chunk = c_chunk.newInstance(world, chunkPrimer, xPos, zPos);
		
		// TODO might be optional
		m_genHeightMap.invoke(chunk);
		
		// TODO if ever doing unloading, this needs to be in ChunkProvider (there will be a bug to fix then as well)
		onChunkLoad(chunk);
		
		return chunk;
		
		// TODO remove
//		Object chunk = c_chunk.newInstance(world, blockArray, metaData, xPos, zPos);
	}
}
