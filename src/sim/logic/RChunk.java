
package sim.logic;

import logging.Log;
import sim.constants.Constants;
import sim.loading.Linker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is an intermediate between the Simulator's high level logic and all of Chunk's reflection
 */
class RChunk {

    private Constructor<?> c_chunk, c_extendedBlockStorage;
	private Method m_genHeightMap, m_addTileEntity, m_onChunkLoad;
	private RChunkPrimer rChunkPrimer;
    private Field f_storageArrays, f_data;
	
	public RChunk(Linker linker, RChunkPrimer rChunkPrimer) throws NoSuchMethodException, SecurityException, IllegalArgumentException, NoSuchFieldException {
		
		prepareChunk(linker);
		this.rChunkPrimer = rChunkPrimer;
		
		Log.i("Preparing Chunks");
	}
	
	/**
	 * Prepares all reflection about to happen
	 */
	private void prepareChunk(Linker linker) throws NoSuchMethodException, SecurityException, NoSuchFieldException {

        Class<?> Chunk = linker.getClass("Chunk");
		Class<?> World = linker.getClass("World");
		Class<?> TileEntity = linker.getClass("TileEntity");
		Class<?> ChunkPrimer = linker.getClass("ChunkPrimer");
        Class<?> ExtendedBlockStorage = linker.getClass("ExtendedBlockStorage");
		
		c_chunk = Chunk.getDeclaredConstructor(World, ChunkPrimer, int.class, int.class);
        c_extendedBlockStorage = ExtendedBlockStorage.getDeclaredConstructor(int.class, boolean.class);

		m_genHeightMap = linker.method("generateSkylightMap", Chunk);
		m_addTileEntity = linker.method("addTileEntity", Chunk, TileEntity);
		m_onChunkLoad = linker.method("onChunkLoad", Chunk);

        f_storageArrays = linker.field("storageArrays", Chunk);

        f_data = ExtendedBlockStorage.getDeclaredField(Constants.EXTENDEDBLOCKSTORAGE_DATA);
        f_data.setAccessible(true);
	}
	
	public void addTileEntity(Object chunk, Object tileEntity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_addTileEntity.invoke(chunk, tileEntity);
	}
	
	void onChunkLoad(Object chunk) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_onChunkLoad.invoke(chunk);
	}
	
	public Object generateEmptyChunk(Object world, int xPos, int zPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Object primer = rChunkPrimer.createChunkPrimer();
		short[] data = new short[65536];
		rChunkPrimer.setData(primer, data);
		
		return createChunk(world, primer, xPos, zPos);
	}

	public Object createChunk(Object world, Object chunkPrimer, int xPos, int zPos) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Object chunk = c_chunk.newInstance(world, chunkPrimer, xPos, zPos);

		// TODO might be optional
		m_genHeightMap.invoke(chunk);

		// If ever doing unloading, this needs to be in ChunkProvider
		onChunkLoad(chunk);

		return chunk;
	}

    public Object[] getStorageArray(Object chunk) throws IllegalAccessException {
        return (Object[]) f_storageArrays.get(chunk);
    }

    public char[] getData(Object[] storageArray, int y) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Object blockStorage = storageArray[y];
        if (blockStorage == null) {
            blockStorage = c_extendedBlockStorage.newInstance(y << 4, false);
            storageArray[y] = blockStorage;
        }

        return (char[]) f_data.get(blockStorage);
    }
}
