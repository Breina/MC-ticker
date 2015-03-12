
package sim.logic;

import logging.Log;
import sim.loading.Linker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is an intermediate between the Simulator's high level logic and all of Chunk's reflection
 */
class RChunk {

    private Constructor<?> c_chunk;
	private Method m_genHeightMap, m_addTileEntity, m_onChunkLoad;
	private RChunkPrimer rChunkPrimer;
	
	public RChunk(Linker linker, RChunkPrimer rChunkPrimer) throws NoSuchMethodException, SecurityException, IllegalArgumentException {
		
		prepareChunk(linker);
		this.rChunkPrimer = rChunkPrimer;
		
		Log.i("Preparing Chunks");
	}
	
	/**
	 * Prepares all reflection about to happen
	 */
	private void prepareChunk(Linker linker) throws NoSuchMethodException, SecurityException {

        Class<?> chunk = linker.getClass("Chunk");
		Class<?> World = linker.getClass("World");
		Class<?> TileEntity = linker.getClass("TileEntity");
		Class<?> ChunkPrimer = linker.getClass("ChunkPrimer");
		
		c_chunk = chunk.getDeclaredConstructor(World, ChunkPrimer, int.class, int.class);

		m_genHeightMap = linker.method("generateSkylightMap", chunk);
		m_addTileEntity = linker.method("addTileEntity", chunk, TileEntity);
		m_onChunkLoad = linker.method("onChunkLoad", chunk);
	}
	
	public void addTileEntity(Object chunk, Object tileEntity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_addTileEntity.invoke(chunk, tileEntity);
	}
	
	void onChunkLoad(Object chunk) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_onChunkLoad.invoke(chunk);
	}
	
	public Object generateEmptyChunk(Object world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Object primer = rChunkPrimer.createChunkPrimer();
		short[] data = new short[65536];
		rChunkPrimer.setData(primer, data);
		
		return createChunk(world, primer, 0, 0);
	}

	public Object createChunk(Object world, Object chunkPrimer, int xPos, int zPos) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object chunk = c_chunk.newInstance(world, chunkPrimer, xPos, zPos);
		
		// TODO might be optional
		m_genHeightMap.invoke(chunk);
		
		// If ever doing unloading, this needs to be in ChunkProvider
		onChunkLoad(chunk);
		
		return chunk;
	}
}
