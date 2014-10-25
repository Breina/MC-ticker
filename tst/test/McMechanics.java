package test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import logging.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import presentation.controllers.SimController;
import sim.constants.Constants;
import sim.constants.Globals;
import sim.logic.SimWorld;
import sim.logic.Simulator;
import utils.Tag;

public class McMechanics {
	
	private Simulator simulator;
	private SimWorld world;
	private SimController controller;
	
	private static final int WATERSPREADSPEED = 3;
	private static final int LAVASPREADSPEED = 15;

	@Before
	public void setUp() throws Exception {
		
		Log.setTest(true);
		simulator = new Simulator(Constants.MCPCONFFOLDER, Globals.getMinecraftFolder());
	}
	
	@After
	public void tearDown() throws Exception {
		
		Log.setTest(false);
	}
	
	private void loadWorld(String schematic) {
		
		File file = new File("schems/tests/" + schematic);
		
		try {
			world = simulator.createWorld();
			world.createInstance();
			controller = new SimController(world);
			
			Tag schematicTag = Tag.readFrom(new FileInputStream(file));
			controller.setSchematic(schematicTag);
			
		} catch (NoSuchAlgorithmException | IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			
			fail("Failed to open " + schematic);
		}
	}
	
	/**
	 * Loads in a cobble generator, destroys lava so it can update and checks
	 * if cobble is formed.
	 */
	@Test
	public void testCobbleGenerator() {
		
		loadWorld("13x3x3-cobble-gen.schematic");
		
		try {
			world.setBlock(10, 1, 1, (byte) 0, (byte) 0);
			
			byte blockId = 0;
			
			for (int i = 0; i < LAVASPREADSPEED; i++) {
				
				world.tickWorld();
				
				blockId = world.getIdFromBlock(
								world.getBlockFromState(
								world.getBlockState(10, 1, 1)));
				
				if (blockId != 0)
					break;
			}
			
			if (blockId != 10)
				fail("Lava did not spread in LAVASPREADSPEED ticks (id=" + blockId + ")");
			
			for (int i = 0; i < LAVASPREADSPEED; i++) {
				
				world.tickWorld();
				
				blockId = world.getIdFromBlock(
						world.getBlockFromState(
						world.getBlockState(9, 1, 1)));
				
				if (blockId != 0)
					break;
			}
			
			if (blockId != 4)
				fail("Cobble was not formed in LAVASPREADSPEED ticks (id=" + blockId + ")");
			
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			
			fail("Failed to test cobble generator");
		}
	}
	
	/**
	 * Places a torch that will break in the water stream
	 */
	@Test
	public void testEntityCreation() {
		
		loadWorld("13x3x3-cobble-gen.schematic");
		
		try {
			world.setBlock(4, 1, 1, (byte) 30, (byte) 0);
			
			for (int i = 0; i < WATERSPREADSPEED; i++)
				world.tickWorld();
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Torch did not survive popping");
		}
	}
}
