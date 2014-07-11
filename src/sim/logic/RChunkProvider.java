package sim.logic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import sim.constants.Constants;
import sim.exceptions.UnimplementedException;
import sim.objects.ChunkCord;

/**
 * This is basically our input into the world. World will get blocks from here.
 */
public class RChunkProvider implements InvocationHandler {
	
	// Buffers all loaded chunks
	private HashMap<ChunkCord, Object> chunks;
	private Object emptyChunk;

	public RChunkProvider() {
		
		chunks = new HashMap<ChunkCord, Object>();
	}
	
	/** Possible TODO
	 * I don't know if it's a smart idea to direct a stacktrace through the reflection part.
	 * The alternative to this would be to call onChunkLoad anywhere outside of chunkProvider.
	 */
	public void addChunk(Object chunk, int x, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
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
	
	public boolean chunkExists(int x, int z) {

		ChunkCord cord = new ChunkCord(x, z);
		
		return chunkExists(cord);
	}
	
	public boolean chunkExists(ChunkCord cord) {
		
		boolean exists = chunks.containsKey(cord);
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println(cord + " exists: " + exists);
		
		return exists;
	}

	public Object getChunk(int x, int z) {

		ChunkCord cord = new ChunkCord(x, z);
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println("Getting chunk " + cord);
		
		if (!chunkExists(cord)) {
			return emptyChunk;
//			throw new IllegalStateException("Non-existing chunk was requested at " + cord);
		}
		
		Object chunk = chunks.get(cord);

		return chunk;
	}

	public String makeString() {
		
		if (Constants.DEBUG_CHUNKPROVIDER)
			System.out.println("Getting chunk provider string");

		return Constants.CHUNKPROVIDERSTRING;
	}

	public int getLoadedChunkCount() {
		
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
			
			// void populate(IChunkProvider var1, int var2, int var3);
			// void recreateStructures(int var1, int var2);
			// void saveExtraData();
			
		} else if (returnType == boolean.class) {
			
			// boolean chunkExists(int var1, int var2);
			if (paramSize == 2)
				if (paramTypes[0] == int.class/* && paramTypes[1] == int.class*/)
					return chunkExists((int) args[0], (int) args[1]);
			
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
				return getChunk((int) args[0], (int) args[1]);			
			
			// List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4);
			// ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5);
			throw new UnimplementedException("List getPossibleCreatures(EnumCreatureType var1, int var2, int var3, int var4)\n" +
					"ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5)");			
		}
		
		throw new IllegalStateException("Should have thrown a NotImplementedException, but that obviously didn't happen... :/");
	}
}