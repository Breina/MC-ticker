package sim.logic;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import logging.Log;
import sim.loading.Linker;

public class Simulator {
	
	// Reflection intermediates
	private RWorld rWorld;
	private RBlock rBlock;
	private RChunk rChunk;
	private RProfiler rProfiler;
	private RChunkProvider rChunkProvider;
	private RTileEntity rTileEntity;
	private RNBTTags rNBTTags;
	private REntity rEntity;
	private RNextTickListEntry rNextTickListEntry;
	private RChunkPrimer rChunkPrimer;

	public Simulator(String mcpFolder, String minecraftFolder) throws ClassNotFoundException, IOException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
	
		// Extracts the classes from the jar, translates them and buffers them
		Log.i("Preparation start");
		
		Linker linker = new Linker(mcpFolder, minecraftFolder);
		
		rProfiler = new RProfiler(linker);
		
		rBlock = new RBlock(linker);
		
		rNBTTags = new RNBTTags(linker);
		
		rTileEntity = new RTileEntity(linker);
		
		rChunk = new RChunk(linker, rBlock);
		
		rChunkPrimer = new RChunkPrimer(linker);
		
		// Our implementation of chunkProvider, which will basically be our block input
		rChunkProvider = new RChunkProvider();
		
		// Making all objects ready, and linking chunkProvider to world already, so chunkProvider will be called from there
		rWorld = new RWorld(linker, rProfiler.getInstance());
		
		rEntity = new REntity(linker);
		
		new RBootstrap(linker).register();
		
		rNextTickListEntry = new RNextTickListEntry(linker);
	}
	
	public SimWorld createWorld() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		return new SimWorld(rBlock, rChunk, rChunkProvider, rEntity, rNBTTags, rNextTickListEntry, rProfiler, rTileEntity, rWorld, rChunkPrimer);
	}
	
//	public String getBlockNameById(byte id) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
//		return rBlock.getBlockName(rBlock.getBlock(id));
//	}
}
