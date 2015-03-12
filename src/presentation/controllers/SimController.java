package presentation.controllers;

import logging.Log;
import presentation.objects.Block;
import presentation.objects.Entity;
import sim.logic.SimWorld;
import utils.Tag;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Largely responsible for catching errors and giving meaningful messages back
 * to the GUI.
 */
public class SimController {
	
	private final SimWorld simWorld;

	public SimController(SimWorld simWorld) {
		this.simWorld = simWorld;
	}
	
	public void setSchematic(Tag schematic) {
		
		try {

			simWorld.setSchematic(schematic);

		} catch (IllegalAccessException | InstantiationException | IOException | InvocationTargetException e) {
			
			Log.e("Failed to send schematic to the simulator " + analyseException(e));
		}
	}
	
	public Tag getSchematic() {
		
		try {
			
			return simWorld.getSchematic();
			
		} catch (IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {

			Log.e("Could not get schematic from the simulator" + analyseException(e));
			return null;
		}
	}
	
	public void createNewWorld(int xSize, int ySize, int zSize) {
		
		try {
			
			simWorld.createEmptyWorld(xSize, ySize, zSize);
		
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not create new world" + analyseException(e));
		}
		
	}
	
	public void saveWorld(OutputStream output) {
		
		try {
			
			simWorld.getSchematic(output);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
			
			Log.e("Could not save world" + analyseException(e));
		}
	}
	
	public boolean tick() {
		
		try {
			
			return simWorld.tickWorld();
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not tick world" + analyseException(e));
		}

		return false;
	}
	
	public void setBlock(int x, int y, int z, byte blockId, byte blockData) {
		
		try {
			
			simWorld.setBlock(x, y, z, blockId, blockData);
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not set block" + analyseException(e));
		}
	}
	
	public void activateBlock(int x, int y, int z) {

		try {
			simWorld.onBlockActivated(x, y, z);

		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {

			Log.e("Could not activate block (" + x + ", " + y + ", " + z + "): " + analyseException(e));
		}
	}

	public void debug(int x, int y, int z) {

		try {
			simWorld.debug(x, y, z);

		} catch (Exception e) {

			Log.e("Could not debug block (" + x + ", " + y + ", " + z + "): " + analyseException(e));
		}
	}

	public Block[][][] getBlockObjects() {

		try {
			return simWorld.getBlockObjects();

		} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {

			Log.e("Could not get blocks from world" + analyseException(e));
		}

		return null;
	}

	public Entity[] getEntityObjects() {

		try {
			return simWorld.getEntityObjects();

		} catch (IllegalAccessException | InvocationTargetException e) {

			Log.e("Could not create view data for entities" + analyseException(e));
		}

		return null;
	}

	public long getWorldTime() {

		return simWorld.getWorldTime();
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

}
