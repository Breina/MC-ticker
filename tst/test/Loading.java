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

import static org.junit.Assert.fail;

public class Loading {
	
	private static final int WORLDTYPEID = 0;
	private static final String WORLDTYPE = "default";
	private static final String GAMETYPE = "CREATIVE";
	private static final long SEED = 0;
	private static final int WORLDPROVIDER = 0;
	private static final boolean HARDCOREENABLED = false;
	private static final int DIFFICULTY = 2;
	private static final boolean CANSSPAWNANIMALS = false;
	private static final boolean CANSPAWNNPCS = false;
	
	private static final String UNICODETEST = "٩(-̮̮̃-̃)۶ ٩(●̮̮̃•̃)۶ ٩(͡๏̯͡๏)۶ ٩(-̮̮̃•̃).";
	
	private Simulator simulator;
	private SimWorld world;
	private SimController controller;

	@Before
	public void setUp() throws Exception {
		
		Log.setTest(true);
		simulator = new Simulator(Constants.MCPCONFFOLDER, Globals.getMinecraftFolder());
	}
	
	@After
	public void tearDown() throws Exception {
		
		Log.setTest(false);
	}
	
	@Test
	public void testSetupSim() {
		
		ensureWorldIsReady();
	}
	
	@Test
	public void testCreateWorldTypeStandard() {
		
		ensureWorldIsReady();
		
		try {
			world.createInstance(0, "default", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(1, "flat", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(2, "largeBiomes", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(3, "amplified", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(8, "default_1_1", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			
			fail("Could not create standard worldType");
		}
	}
	
	@Test
	public void testCreateWorldTypeCustom() {
		
		ensureWorldIsReady();
		
		try {
			for (int i = 0; i < 15; i++)
				switch (i) {
					case 0:
					case 1:
					case 2:
					case 3:
					case 8:
						continue;
					default:
						world.createInstance(i, UNICODETEST, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
				}
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			
			fail("Could not create custom worldType");
		}
	}
	
	@Test(expected=AssertionError.class)
	public void testCreateWorldTypeOutOfRange() {

		ensureWorldIsReady();
		
		try {
			world.createInstance(0, "minusOneWorldType", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(-1, "minusOneWorldType", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(16, "sixteenWorldType", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(Integer.MAX_VALUE, "integerMaxWorldType", GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			
			fail("Could not create out of range worldType");
		}
	}
	
	@Test
	public void testCreateGameTypeStandard() {
		
		ensureWorldIsReady();

		try {
			world.createInstance(WORLDTYPEID, WORLDTYPE, "SURVIVAL", SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, "CREATIVE", SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, "ADVENTURE", SEED, WORLDPROVIDER, HARDCOREENABLED);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Could not create standard gameType");
		}
	}
	
	@Test
	public void testCreateGameTypeNotSet() {
		
		ensureWorldIsReady();

		try {
			world.createInstance(WORLDTYPEID, WORLDTYPE, "NOT_SET", SEED, WORLDPROVIDER, HARDCOREENABLED);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Could not create standard gameType");
		}
	}
	
	@Test(expected=AssertionError.class)
	public void testCreateGameTypeCustom() {
		
		ensureWorldIsReady();

		try {
			
			world.createInstance(WORLDTYPEID, WORLDTYPE, "CUSTOM", SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, UNICODETEST, SEED, WORLDPROVIDER, HARDCOREENABLED);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Could not create custom gameType");
		}
	}
	
	@Test
	public void testCreateSeed() {
		
		ensureWorldIsReady();
		
		try {
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, 0, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, -1, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, Integer.MAX_VALUE, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, Integer.MIN_VALUE, WORLDPROVIDER, HARDCOREENABLED);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Could not plant seeds");
		}
	}
	
	@Test
	public void testCreateHardcore() {
		
		ensureWorldIsReady();
		
		try {
			
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, false);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, true);
		
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			
			fail("Could not create hardcore");
		}
	}
	
	@Test
	public void testCreateDifficultyStandard() {
		
		ensureWorldIsReady();

		try {
			
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			
			fail("Could not create difficulty");
		}
	}
	
	@Test
	public void testCreateDifficultyCustom() {
		
		ensureWorldIsReady();

		try {
			
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			
			fail("Could not create difficulty");
		}
	}

	@Test
	public void testCanSpawnAnimals() {

		ensureWorldIsReady();

		try {
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);

		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Could not spawn animals");
		}
	}

	@Test
	public void testCanSpawnNPCS() {

		ensureWorldIsReady();

		try {
			world.createInstance(WORLDTYPEID, WORLDTYPE, GAMETYPE, SEED, WORLDPROVIDER, HARDCOREENABLED);

		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {

			fail("Could spawn NPCS");
		}
	}
	
	private void ensureWorldIsReady() {
		
		if (world != null && controller != null)
			return;
		
		try {
			world = simulator.createWorld();
			world.createInstance();
			controller = new SimController(world);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			
			world = null;
			controller = null;
			fail("Could not create world");
		}
	}
	
	private void openSchematic(String path) {
		
		File file = new File("schems/tests/" + path);
		
		try {
			Tag schematic = Tag.readFrom(new FileInputStream(file));
			controller.setSchematic(schematic);
			
		} catch (NoSuchAlgorithmException | IOException e) {
			
			fail("Failed to open " + path);
		}
	}

	@Test
	public void testEmptyBox() {
		
		ensureWorldIsReady();
		
		openSchematic("3x3x3-box.schematic");
	}
	
	@Test
	public void testMultiChunk() {
		
		ensureWorldIsReady();
		
		openSchematic("32x32x32-box.schematic");
	}
	
	@Test
	public void testTileEntities() {
		
		ensureWorldIsReady();
		
		openSchematic("3x3x3-box-sign.schematic");
	}
	
	@Test
	public void testEntities() {
		
		ensureWorldIsReady();
		
		openSchematic("3x3x3-box-chicken.schematic");
	}
}