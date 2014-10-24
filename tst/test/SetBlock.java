package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import logging.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import presentation.controllers.SimController;

import sim.constants.Constants;
import sim.constants.Globals;
import sim.logic.SimWorld;
import sim.logic.Simulator;
import utils.Tag;

public class SetBlock {
	
	private static final int MAXBLOCKID = 197;
	
	private Simulator simulator;
	private SimWorld world;
	private SimController controller;
	
	private static final String SCHEMATIC = "3x3x3-box.schematic";

	@Before
	public void setUp() throws Exception {
		
		Log.setTest(true);
		simulator = new Simulator(Constants.MCPCONFFOLDER, Globals.getMinecraftFolder());
	}
	
	@After
	public void tearDown() throws Exception {
		
		Log.setTest(false);
	}
	
	private void ensureWorldIsReady() {
		
		File file = new File("schems/tests/" + SCHEMATIC);
		
		try {
			world = simulator.createWorld();
			world.createInstance();
			controller = new SimController(world);
			
			Tag schematic = Tag.readFrom(new FileInputStream(file));
			controller.setSchematic(schematic);
			
		} catch (NoSuchAlgorithmException | IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			
			fail("Failed to open " + SCHEMATIC);
		}
	}

	@Test
	public void testSetBlock() {
		
		ensureWorldIsReady();
		
		int errors = 0;
		
		for (int id = 0; id < MAXBLOCKID; id++) {
			
			boolean metaAllowedMessage = false;
			
			for (int data = 0; data < 16; data++) {
				
				try {
					// Set block
					world.setBlock(1, 1, 1, (byte) id, (byte) data);
					
					// Get block
					Object blockState = world.getBlockState(1, 1, 1);
					Object block = world.getBlockFromState(blockState);
					
					byte getBlockId = world.getIdFromBlock(block);
					byte getBlockData = world.getDataFromState(block, blockState);
					
					if (data != getBlockData) {
						
						if (id != getBlockId || getBlockData != 0) {
						
							StringBuilder sb = new StringBuilder();
							
							sb.append("SetBlock / GetBlock mismatch (id/data): ");
							sb.append(id >= 0 ? id : id + 128);
							sb.append('/');
							sb.append(data);
							sb.append(" -> ");
							sb.append(getBlockId);
							sb.append('/');
							sb.append(getBlockData);
							
							System.out.println(sb.toString());
							
							errors++;
							
						} else {
							
							if (!metaAllowedMessage) {
								
								StringBuilder sb = new StringBuilder();
								
								sb.append("Meta data not allowed for id: ");
								sb.append(id);
								
								System.out.println(sb.toString());
								
								metaAllowedMessage = true;
							}
						}
					}
					
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException
						| InstantiationException e) {
					
					StringBuilder sb = new StringBuilder();
					
					sb.append("Could not setBlock with id=");
					sb.append(id >= 0 ? id : id + 128);
					sb.append(" data=");
					sb.append(data);
					sb.append(": ");
					sb.append(e.getCause().getClass());
					
					System.out.println(sb.toString());
					
					errors++;
					
					ensureWorldIsReady();
				}
			}
		}
		
		if (errors != 0) {
			
			fail("Setblock failed with " + errors + " errors");
		}
	}
}
