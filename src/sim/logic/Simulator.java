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
	private RBlockPos rBlockPos;

	public Simulator(String mcpFolder, String minecraftFolder) throws ClassNotFoundException, IOException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
	
		// Extracts the classes from the jar, translates them and buffers them
		Log.i("Preparation start");
		
		Linker linker = new Linker(mcpFolder, minecraftFolder);
		
		rProfiler = new RProfiler(linker);
		
		rBlock = new RBlock(linker);
		
		rBlockPos = new RBlockPos(linker);
		
		rNBTTags = new RNBTTags(linker);
		
		rTileEntity = new RTileEntity(linker);
		
		rChunkPrimer = new RChunkPrimer(linker);
		
		rChunk = new RChunk(linker, rBlock, rChunkPrimer);
		
		// Our implementation of chunkProvider, which will basically be our block input
		rChunkProvider = new RChunkProvider();
		
		// Making all objects ready, and linking chunkProvider to world already, so chunkProvider will be called from there
		rWorld = new RWorld(linker, rProfiler.getInstance(), rBlockPos);
		
		rEntity = new REntity(linker);
		
		new RBootstrap(linker).register();
		
		rNextTickListEntry = new RNextTickListEntry(linker);
		
		Log.i("Done loading");
	}
	
	public SimWorld createWorld() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		return new SimWorld(rBlock, rChunk, rChunkProvider, rEntity, rNBTTags, rNextTickListEntry, rProfiler, rTileEntity, rWorld, rChunkPrimer, rBlockPos);
	}
	
//	public String getBlockNameById(byte id) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
//		return rBlock.getBlockName(rBlock.getBlock(id));
//	}
}
