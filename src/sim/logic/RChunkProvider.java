package sim.logic;

import logging.Log;
import sim.constants.Constants;
import sim.exceptions.UnimplementedException;
import sim.objects.ChunkCord;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * This is basically our input into the world. World will get blocks from here.
 */
class RChunkProvider implements InvocationHandler {
	
	// Buffers all loaded chunks
	private final HashMap<ChunkCord, Object> chunks;
	public Object emptyChunk;

	private final RBlockPos rBlockPos;

	public RChunkProvider(RBlockPos rBlockPos) {
		
		chunks = new HashMap<>();
		this.rBlockPos = rBlockPos;
	}

	public void addChunk(Object chunk, int x, int z) throws IllegalArgumentException {

        ChunkCord cord = new ChunkCord(x, z);
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println("Adding chunk " + cord);
		
		chunks.put(cord, chunk);
	}
	
	public void clear() {
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println("Clearing");
		
		chunks.clear();
	}
	
	boolean chunkExists(int x, int z) {

		ChunkCord cord = new ChunkCord(x, z);
		
		return chunkExists(cord);
	}
	
	boolean chunkExists(ChunkCord cord) {
		
		boolean exists = chunks.containsKey(cord);
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println(cord + " exists: " + exists);
		
		return exists;
	}

	public Object getChunk(int x, int z) {

		// TODO wtf was I thinking, use a long key for the hashmap!!
		ChunkCord cord = new ChunkCord(x, z);
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println("Getting chunk " + cord);
		
		if (!chunkExists(cord)) {
			return emptyChunk;
		}

        return chunks.get(cord);
	}

	Object getChunk(Object blockPos) {

		try {
			int x = rBlockPos.getX(blockPos) << 4;
			int z = rBlockPos.getZ(blockPos) << 4;

			return getChunk(x, z);

		} catch (IllegalAccessException e) {

			Log.e("Failed to provide chunk for " + blockPos);
			return null;
		}
	}

	String makeString() {
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println("Getting chunk provider string");

		return Constants.CHUNKPROVIDERSTRING;
	}

	int getLoadedChunkCount() {
		
		int count = chunks.size();
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println("Getting size: " + count);

		return count;
	}
	
	public void setEmptyChunk(Object emptyChunk) {
		
		this.emptyChunk = emptyChunk;
	}

	/**
	 * There is no way to tell the called Method by its single letter name due to perforations of other methods.
	 * So we're comparing return types and parameters, this however is bound to break, so
	 * TODO Extract class names together with method names and then properly reverse translate
	 * (there's commented code for that in Constants) 
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println("Invoked: " + method.toString());
		
		Class<?>[] paramTypes = method.getParameterTypes();
		int paramSize = paramTypes.length;
		Class<?> returnType = method.getReturnType();
		
		if (returnType == void.class) {
			
			// void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
			// void populate(IChunkProvider var1, int var2, int var3);
			// void saveExtraData();
			
		} else if (returnType == boolean.class) {
			
			// boolean chunkExists(int var1, int var2);
			if (paramSize == 2)
				if (paramTypes[0] == int.class && paramTypes[1] == int.class)
					return chunkExists((int) args[0], (int) args[1]);
			
			// boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
			// boolean saveChunks(boolean var1, IProgressUpdate var2);
				else
					throw new UnimplementedException("boolean saveChunks(boolean var1, IProgressUpdate var2)");
			
			// boolean unloadQueuedChunks();		There is no way to distingish this from canSave, should throw NotImpExcptn instead
			// boolean canSave();
			return false;
			
		} else if (returnType == int.class) {
			
			// int getLoadedChunkCount();
			return getLoadedChunkCount();
			
		} else if (returnType == String.class) {
			
			// String makeString();
			return makeString();
			
		} else {
			
			// Chunk provideChunk(int var1, int var2);
			// Chunk loadChunk(int var1, int var2);
			if (paramSize == 2)
				if (paramTypes[0] == int.class && paramTypes[1] == int.class)
					return getChunk((int) args[0], (int) args[1]);

			if (paramSize == 1)
				return getChunk(args[0]);
			
			// BlockPos func_180513_a(World worldIn, String p_180513_2_, BlockPos p_180513_3_)
			throw new UnimplementedException("METHBOD: " + method + "\nList getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4)\n" +
					"ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5)");
		}

		throw new IllegalStateException("Should have thrown a NotImplementedException, but that obviously didn't happen... :/");
	}
}