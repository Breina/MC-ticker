package sim.controller;

import gui.main.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import javax.xml.ws.soap.AddressingFeature.Responses;

import logging.Log;
import sim.logic.Simulator;

/**
 * Largely responsible for catching errors and giving meaningful messages back
 * to the GUI.
 */
public class Sim {

	private boolean ready;
	private Simulator simulator;
	
	private static final Sim controller = new Sim();
	private static final Response NOTREADYRESPONSE = new Response(Response.Type.ERROR, "The Simulator hasn't initialized yet.");

	public static Sim getController() {
		return controller;
	}
	
	public Sim() {

		ready = false;

	}

	public Response initialize(String mcpFolder, String minecraftFolder) {		
		
		try {
			
			simulator = new Simulator(mcpFolder, minecraftFolder);
			
			ready = true;
			
			return new Response(Response.Type.SUCCESS);

		} catch (IllegalAccessException | ClassNotFoundException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchFieldException | NoSuchMethodException | SecurityException e) {
			
			return new Response(Response.Type.ERROR, "Could not initlialize the Simulator" + analyseException(e), e);
		}
	}
	
	public void destroy(String worldName) {
		
		simulator.destroy(worldName);
	}
	
	public Response createNewWorld(String worldName, int xSize, int ySize, int zSize) {
		
		if (!ready)
			return NOTREADYRESPONSE;
		
		try {
			
			simulator.createEmptyWorld(worldName, xSize, ySize, zSize);
			
			return new Response(Response.Type.SUCCESS);
		
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			return new Response(Response.Type.ERROR, "Could not create new world" + analyseException(e), e);
		}
		
	}
	
	public Response loadWorld(String worldName, InputStream input) throws NoSuchAlgorithmException {
		
		if (!ready)
			return NOTREADYRESPONSE;
		
		try {
			
			simulator.loadWorld(worldName, input);
			
			return new Response(Response.Type.SUCCESS);
			
		} catch (IllegalAccessException | IOException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			return new Response(Response.Type.ERROR, "Could not load world" + analyseException(e), e);	
		}
	}
	
	public Response saveWorld(String worldName, OutputStream output) {
		
		if (!ready)
			return NOTREADYRESPONSE;
		
		try {
			
			simulator.saveWorld(worldName, output);
			
			return new Response(Response.Type.SUCCESS);
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException | IOException e) {
			
			return new Response(Response.Type.ERROR, "Could not save world" + analyseException(e), e);
		}
	}
	
	public Response tick(String worldName) {
		
		if (!ready)
			return NOTREADYRESPONSE;
		
		try {
			
			simulator.tickWorld(worldName);
			
			return new Response(Response.Type.SUCCESS);
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			return new Response(Response.Type.ERROR, "Could not tick world" + analyseException(e), e);
		}
	}
	
	public Response setBlock(String worldName, int x, int y, int z, byte id, byte data) {
		
		if (!ready)
			return NOTREADYRESPONSE;
		
		try {
			
			simulator.setBlock(worldName, x, y, z, id, data);
			
			return new Response(Response.Type.SUCCESS);
			
		} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
			
			return new Response(Response.Type.ERROR, "Could not set block" + analyseException(e), e);
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
		
		Log.e(msg);
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
