package presentation.controllers;

import logging.Log;
import sim.constants.Constants;
import sim.logic.SimWorld;
import sim.objects.WorldState;
import utils.Tag;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

/**
 * Largely responsible for catching errors and giving meaningful messages back
 * to the GUI.
 */
public class SimController {
	
	private SimWorld simWorld;

	public SimController(SimWorld simWorld) {
		this.simWorld = simWorld;
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
			
			return simWorld.getWorldTag();
			
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
	
	public void loadWorld(Tag input) throws NoSuchAlgorithmException {
		
		try {
			
			simWorld.setWorld(input);
			
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not load world" + analyseException(e));	
		}
	}
	
	public void saveWorld(OutputStream output) {
		
		try {
			
			simWorld.getWorld(output);
			
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

	public void setState(WorldState state) {
		
		try {
			if (Constants.DEBUG_STATE)
				Log.i("Setting state to time " + state.getWorldTime());
			
			simWorld.setState(state);
			
		} catch (ArrayIndexOutOfBoundsException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException | IOException e) {
			
			Log.e("Could not set state" + analyseException(e));
		}
	}
	
	public WorldState getState() {
		
		try {
			if (Constants.DEBUG_STATE)
				Log.i("Getting state from time " + simWorld.getState().getWorldTime());
			
			return simWorld.getState();
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			
			Log.e("Could not get state" + analyseException(e));
		}
		
		return null;
	}
}
