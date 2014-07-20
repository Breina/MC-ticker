package gui.controllers;

import gui.exceptions.SchematicException;
import gui.objects.WorldData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import logging.Log;
import sim.controller.Response;
import sim.controller.Response.Type;
import sim.logic.Simulator;
import utils.CircularByteBuffer;
import utils.Tag;

/**
 * Largely responsible for catching errors and giving meaningful messages back
 * to the GUI.
 */
public class SimController {
	
	private Simulator simulator;

	public SimController() {
	}
	
	public void setSchematic(WorldData worldData) {
		
		try {
			
			CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
			worldData.saveSchematic(cbb.getOutputStream());
			loadWorld(worldData.getName(), cbb.getInputStream());
			
		} catch (SchematicException | IOException | NoSuchAlgorithmException e) {
			
			Log.e("Failed to send schematic to the simulator " + analyseException(e));
		}
	}
	
	public Tag getSchematic(WorldData worldData) {
		
		try {
			
			CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
			saveWorld(worldData.getName(), cbb.getOutputStream());
			
			return Tag.readFrom(cbb.getInputStream());
			
		} catch (NoSuchAlgorithmException | IOException e) {

			Log.e("Could not get schematic from the simulator" + analyseException(e));
			return null;
		}
	}

	public void initialize(String mcpFolder, String minecraftFolder) {		
		
		try {
			
			simulator = new Simulator(mcpFolder, minecraftFolder);

		} catch (IllegalAccessException | ClassNotFoundException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchFieldException | NoSuchMethodException | SecurityException e) {
			
			Log.e("Could not initlialize the Simulator" + analyseException(e));
		}
	}
	
	public void destroy(String worldName) {
		
		simulator.destroy(worldName);
	}
	
	public void createNewWorld(String worldName, int xSize, int ySize, int zSize) {
		
		try {
			
			simulator.createEmptyWorld(worldName, xSize, ySize, zSize);
		
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not create new world" + analyseException(e));
		}
		
	}
	
	public void loadWorld(String worldName, InputStream input) throws NoSuchAlgorithmException {
		
		try {
			
			simulator.loadWorld(worldName, input);
			
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not load world" + analyseException(e));	
		}
	}
	
	public void saveWorld(String worldName, OutputStream output) {
		
		try {
			
			simulator.saveWorld(worldName, output);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
			
			Log.e("Could not save world" + analyseException(e));
		}
	}
	
	public void tick(String worldName) {
		
		try {
			
			simulator.tickWorld(worldName);
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not tick world" + analyseException(e));
		}
	}
	
	public void setBlock(String worldName, int x, int y, int z, byte id, byte data) {
		
		try {
			
			simulator.setBlock(worldName, x, y, z, id, data);
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			Log.e("Could not set block" + analyseException(e));
		}
	}
	
	private String analyseException(Exception e) {
		
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

}
