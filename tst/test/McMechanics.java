package test;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

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
		
		loadWorld("cobble-gen.schematic");
		
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
		
		loadWorld("cobble-gen.schematic");
		
		try {
			world.setBlock(4, 1, 1, (byte) 30, (byte) 0);
			
			for (int i = 0; i < WATERSPREADSPEED; i++)
				world.tickWorld();
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Torch did not survive popping");
		}
	}
	
	/**
	 * Tests if a clock still runs after loading
	 */
	@Test
	public void testTileTick() {
		
		loadWorld("repeater-clock.schematic");
		
		try {
			Object prevState = world.getBlockState(1, 1, 1);
			byte prevData = world.getDataFromState(world.getBlockFromState(prevState), prevState);
			
			world.tickWorld();

			Object nextState = world.getBlockState(1, 1, 1);
			byte nextData = world.getDataFromState(world.getBlockFromState(nextState), nextState);

			assertNotEquals(prevData, nextData);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Clock does not seem to be running");
		}
	}

	/**
	 * Test activators
	 */
	@Test
	public void testActivation() {

		loadWorld("activators.schematic");
/*
			activator		lamp
			1,1,1 button	2,1,1
			1,1,2 lever		2,1,2
			2,2,3 plate		2,1,3
*/
		try {
			// Get prev data
			Object prevBtnState = world.getBlockState(1, 1, 1);
			byte prevBtnData = world.getDataFromState(world.getBlockFromState(prevBtnState), prevBtnState);

			Object prevBtnLampState = world.getBlockState(2, 1, 1);
			byte prevBtnLampData = world.getDataFromState(world.getBlockFromState(prevBtnLampState), prevBtnLampState);

			Object prevLvrState = world.getBlockState(1, 1, 2);
			byte prevLvrData = world.getDataFromState(world.getBlockFromState(prevLvrState), prevLvrState);

			Object prevLvrLampState = world.getBlockState(2, 1, 2);
			byte prevLvrLampData = world.getDataFromState(world.getBlockFromState(prevLvrLampState), prevLvrLampState);

			Object prevPltState = world.getBlockState(2, 2, 3);
			byte prevPltData = world.getDataFromState(world.getBlockFromState(prevPltState), prevPltState);

			Object prevPltLampState = world.getBlockState(2, 1, 3);
			byte prevPltLampData = world.getDataFromState(world.getBlockFromState(prevPltLampState), prevPltLampState);

			// Activate
//			world.onBlockActivated(1, 1, 1);
			world.onBlockActivated(1, 1, 2);
			world.onBlockActivated(2, 2, 3);

			/* copy paste for visibility
			activator		lamp
			1,1,1 button	2,1,1
			1,1,2 lever		2,1,2
			2,2,3 plate		2,1,3
			*/

			// Get new data
			Object nextBtnState = world.getBlockState(1, 1, 1);
			byte nextBtnData = world.getDataFromState(world.getBlockFromState(nextBtnState), nextBtnState);

			Object nextBtnLampState = world.getBlockState(2, 1, 1);
			byte nextBtnLampData = world.getDataFromState(world.getBlockFromState(nextBtnLampState), nextBtnLampState);

			Object nextLvrState = world.getBlockState(1, 1, 2);
			byte nextLvrData = world.getDataFromState(world.getBlockFromState(nextLvrState), nextLvrState);

			Object nextLvrLampState = world.getBlockState(2, 1, 2);
			byte nextLvrLampData = world.getDataFromState(world.getBlockFromState(nextLvrLampState), nextLvrLampState);

			Object nextPltState = world.getBlockState(2, 2, 3);
			byte nextPltData = world.getDataFromState(world.getBlockFromState(nextPltState), nextPltState);

			Object nextPltLampState = world.getBlockState(2, 1, 3);
			byte nextPltLampData = world.getDataFromState(world.getBlockFromState(nextPltLampState), nextPltLampState);

			// Compare
//			assertNotEquals(prevBtnData		  ,	nextBtnData		);
//			assertNotEquals(prevBtnLampData   ,	nextBtnLampData	);
			assertNotEquals(prevLvrData  	  ,	nextLvrData	);
			assertNotEquals(prevLvrLampData   ,	nextLvrLampData	);
			assertNotEquals(prevPltData  	  ,	nextPltData		);
			assertNotEquals(prevPltLampData   ,	nextPltLampData	);

		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			System.out.println(e.getCause());

			fail("Activation did not succeed");
		}
	}
}
