package sim.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import logging.Log;
import sim.constants.Constants;
import sim.exceptions.SchematicException;
import sim.loading.Linker;
import sim.objects.WorldInstance;
import sim.objects.WorldState;
import utils.Tag;
import utils.Tag.Type;

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

	public Simulator(String mcpFolder, String minecraftFolder) throws ClassNotFoundException, IOException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
	
		// Extracts the classes from the jar, translates them and buffers them
		Log.i("Preparation start");
		
		Linker linker = new Linker(mcpFolder, minecraftFolder);
		
		rProfiler = new RProfiler(linker);
		
		rBlock = new RBlock(linker);
		
		rNBTTags = new RNBTTags(linker);
		
		rTileEntity = new RTileEntity(linker, rNBTTags.getReflClass());
		
		rChunk = new RChunk(linker, rBlock, rTileEntity.getReflClass());
		
		// Our implementation of chunkProvider, which will basically be our block input
		rChunkProvider = new RChunkProvider();
		
		// Making all objects ready, and linking chunkProvider to world already, so chunkProvider will be called from there
		rWorld = new RWorld(linker, rProfiler.getInstance());
		
		rEntity = new REntity(linker, rNBTTags.getReflClass(), rWorld.getReflClass());
		
		new RBootstrap(linker).register();
		
		rNextTickListEntry = new RNextTickListEntry(linker);
	}
	
	public SimWorld createWorld() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		return new SimWorld(rBlock, rChunk, rChunkProvider, rEntity, rNBTTags, rNextTickListEntry, rProfiler, rTileEntity, rWorld);
	}
	
	// Just so that I have at least one method here after refactoring
	public String getBlockNameById(byte id) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return rBlock.getBlockName(rBlock.getBlock(id));
	}
}
