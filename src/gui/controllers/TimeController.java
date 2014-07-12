package gui.controllers;

import gui.bettergui.TimeWindow;
import gui.bettergui.time.PlayState;
import gui.exceptions.SchematicException;
import gui.objects.WorldData;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import logging.Log;
import sim.controller.Sim;
import utils.CircularByteBuffer;
import utils.Tag;


public class TimeController implements Runnable {
	
	private List<Tag> timeLine;
	private WorldController controller;
	private WorldData worldData;
	
	private HashSet<Integer> foundHashes;
	private int index;
	private boolean goForward, isPaused, hasDelay, endFound;
	
	private TimeWindow window;
	
	private Thread thread;
	
	public TimeController(WorldController controller) {
		
		this.controller = controller;
		worldData = controller.getWorldData();
		window = controller.getTimeWindow();
		
		timeLine = new ArrayList<>();
		foundHashes = new HashSet<>();
	}
	
	public void init() {
		index = 0;
		isPaused = true;
		endFound = false;
		
//		thread = new Thread(this);
//		thread.start();
		
		try {
			Tag schematic = getSchemFromSim();
			
			worldData.loadSchematic(schematic);
			controller.updateWithNewData();
			
			foundHashes.clear();
			timeLine.clear();
			
			foundHashes.add(schematic.hashCode());
			timeLine.add(schematic);
			
			prepareTimeLine(100);
			setPlaystate(PlayState.PAUSED);
			
		} catch (NoSuchAlgorithmException | SchematicException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareTimeLine(int max) {
		
		for (int i = 0; i < max; i++)
			if (!tick()) {
				
				endFound = true;
				return;
			}
	}
	
	private boolean tick() {
		
		Sim.getController().tick(worldData.getName());
		
		Tag schematic;
		try {
			schematic = getSchemFromSim();
			
			if (!foundHashes.contains(schematic.hashCode())) {

				timeLine.add(schematic);
				foundHashes.add(schematic.hashCode());
				
				return true;
			}
			
		} catch (NoSuchAlgorithmException | IOException | SchematicException e) {
			Log.e("Failed to get schematic from the simulator: " + e.getMessage());
		}
		
		return false;
	}
	
	private Tag getSchemFromSim() throws NoSuchAlgorithmException, IOException, SchematicException {
		
		CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		Sim.getController().saveWorld(worldData.getName(), cbb.getOutputStream());
//		worldData.loadSchematic(cbb.getInputStream());
		
		return Tag.readFrom(cbb.getInputStream());
	}
	
	public void setPlaystate(PlayState playState) {
		
		switch (playState) {
			
			case START:
			case RUSHBACK:
			case PLAYBACK:
			case STEPBACK:
				// TODO
				break;
				
			case PAUSED:
				isPaused = true;
				break;
				
			case STEPFORWARD:
				isPaused = true;
				goForward = true;
				hasDelay = false;
				
				notify();
				break;
				
			case PLAYFORWARD:
				isPaused = false;
				goForward = true;
				hasDelay = true;
				
				notify();
				break;
				
			case RUSHFORWARD:
				isPaused = false;
				goForward = true;
				hasDelay = false;
				
				notify();
				break;
				
			case END:
				isPaused = true;
				hasDelay = false;
				
				if (endFound)
					index = timeLine.size() - 1;
		}
	}

	@Override
	public void run() {
		
		// Hardcore thread never ends
		
		try {
			for (;;) {
				
				if (isPaused)
					wait();
				
				if (goForward)
					index++;
				else
					index--;
				
				if (index < 0 || index >= timeLine.size()) {
					Log.e("The time controller tried to access uncalculated times.");
					return;
				}
				
				worldData.loadSchematic(timeLine.get(index));
				controller.updateWithNewData();
				
				if (!hasDelay)
					wait(100l);
			}
		} catch (InterruptedException e) {
			Log.e("The time controller's thread was rudely abrupted! :o");			
			
		} catch (SchematicException | IOException e) {
			Log.e("Could not read precomputed schematic.");
		}
	}
}
