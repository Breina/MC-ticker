package gui.controllers;

import gui.exceptions.SchematicException;
import gui.objects.ViewData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import logging.Log;
import sim.logic.Simulator;
import sim.logic.World;
import sim.objects.WorldState;
import utils.CircularByteBuffer;
import utils.Tag;

/**
 * Largely responsible for catching errors and giving meaningful messages back
 * to the GUI.
 */
public class SimController {
	
	private World world;

	public SimController(World world) {
		this.world = world;
	}
	
	public void setSchematic(Tag schematic) {
		
		try {
			
			loadWorld(schematic);
			
		} catch (NoSuchAlgorithmException e) {
			
			Log.e("Failed to send schematic to the simulator " + analyseException(e));
		}
	}
	
	public Tag getSchematic() {
		
		try {
			
			CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
			saveWorld(cbb.getOutputStream());
			
			return Tag.readFrom(cbb.getInputStream());
			
		} catch (NoSuchAlgorithmException | IOException e) {

			Log.e("Could not get schematic from the simulator" + analyseException(e));
			return null;
		}
	}
	
	public void createNewWorld(int xSize, int ySize, int zSize) {
		
		try {
			
			world.createEmptyWorld(xSize, ySize, zSize);
		
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not create new world" + analyseException(e));
		}
		
	}
	
	public void loadWorld(Tag input) throws NoSuchAlgorithmException {
		
		try {
			
			world.setWorld(input);
			
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not load world" + analyseException(e));	
		}
	}
	
	public void saveWorld(OutputStream output) {
		
		try {
			
			world.getWorld(output);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
			
			Log.e("Could not save world" + analyseException(e));
		}
	}
	
	public void tick() {
		
		try {
			
			world.tickWorld();
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not tick world" + analyseException(e));
		}
	}
	
	public void setBlock(int x, int y, int z, byte blockId, byte blockData) {
		
		try {
			
			world.setBlock(x, y, z, blockId, blockData);
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not set block" + analyseException(e));
		}
	}
	
	public static String analyseException(Exception e) {
		
		String msg;
		
		if (e instanceof IllegalAccessException
				|| e instanceof SecurityException
				|| e instanceof InstantiationException
				|| e instanceof ArrayIndexOutOfBoundsException
				|| e instanceof NoSuchFieldException)
			
			msg = "Internal reflection error, are we using the correct version? " + e.getCause();
		
		else if (e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException)
			
			msg = "Internal reflection error, are we using the correct version? " + e.getMessage();
		
		else if (e instanceof ClassNotFoundException)
			
			msg = "One or more classes were not found, are we using the right version? " + e.getMessage();
		
		else if (e instanceof IOException)
			
			msg = "File exception: " + e.getMessage();
		
		else if (e instanceof InvocationTargetException) {
			
			Throwable cause = e.getCause();

			if (cause == null)
				throw new IllegalStateException("Got InvocationTargetException, but the cause is null.", e);

			else if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;

			else if (cause instanceof Exception)
				msg = "Invocation failed with cause: " + cause;

			else
				msg = "Invokation failed with error: " + cause;
		} else {
			msg = "Other error: " + e.getClass();
		}
		
		e.printStackTrace();
		
		return ":\n" + msg;
	}
	
	// TODO testing
//	public static void main(String[] args) throws FileNotFoundException {
//		
//		Sim sim = new Sim();
//		
//		sim.initialize(Constants.MCPCONFFOLDER, new File(Constants.JARPATH));
//		sim.loadWorld("test", new FileInputStream(new File("C:/temp/schems/comptest.schematic")));
//		sim.setBlock("test", 2, 1, 2, (byte) 76, (byte) 5);
//	}

	public void setState(WorldState state) {
		
		try {
			
			world.setState(state);
			
		} catch (ArrayIndexOutOfBoundsException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException | IOException e) {
			
			Log.e("Could not set state" + analyseException(e));
		}
	}
	
	public WorldState getState() {
		
		try {
			
			return world.getState();
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			
			Log.e("Could not get state" + analyseException(e));
		}
		
		return null;
	}
}
