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
import utils.CircularByteBuffer;
import utils.Tag;


public class TimeController implements Runnable {
	
	private List<Tag> timeLine;
	
	private WorldController worldController;
	private SimController simController;
	
	private WorldData worldData;
	
	private HashSet<Integer> foundHashes;
	private int index;
	private boolean goForward, isPaused, hasDelay, endFound;
	
	private TimeWindow window;
	
	private Thread thread;
	
	public TimeController(WorldController worldController) {
		
		this.worldController = worldController;
		this.simController = worldController.getMainController().getSimController();
		
		worldData = worldController.getWorldData();
		
		timeLine = new ArrayList<>();
		foundHashes = new HashSet<>();
		
		setPlaystate(PlayState.PAUSED);
	}
	
	public void init() {
		index = 0;
		isPaused = true;
		endFound = false;
		
		try {
			Tag schematic = simController.getSchematic(worldData);
			
			worldData.loadSchematic(schematic);
			worldController.updateWithNewData();
			
			foundHashes.clear();
			timeLine.clear();
			
			foundHashes.add(schematic.hashCode());
			timeLine.add(schematic);
			
			window.setStep(0);
			window.setEndFound(false);
			
			prepareTimeLine(100);
			
			thread = new Thread(this);
			thread.start();
			
		} catch (SchematicException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareTimeLine(int max) {
		
		for (int i = 0; i < max; i++)
			if (!tick()) {
				
				endFound = true;
				window.setEndFound(true);
				return;
			}
	}
	
	private boolean tick() {
		
		simController.tick(worldData.getName());
		
		Tag schematic = simController.getSchematic(worldData);
			
		if (!foundHashes.contains(schematic.hashCode())) {

			timeLine.add(schematic);
			foundHashes.add(schematic.hashCode());
			
			return true;
		}
		
		return false;
	}
	
	public synchronized void setPlaystate(PlayState playState) {
		
		switch (playState) {
			
			case START:
				isPaused = true;
				hasDelay = false;
				
				index = 0;
				break;
				
			case RUSHBACK:
				isPaused = false;
				goForward = false;
				hasDelay = false;
				
				notify();
				break;
				
			case PLAYBACK:
				isPaused = false;
				goForward = false;
				hasDelay = true;
				
				notify();
				break;
				
			case STEPBACK:
				isPaused = true;
				goForward = false;
				hasDelay = false;
				
				notify();
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
	public synchronized void run() {
		
		// Hardcore thread never ends
		
		try {
			for (;;) {
				
				if (isPaused)
					wait();
				
				if (goForward)
					index++;
				else
					index--;
				
				if (index <= 0 ) {
					index = 0;
					setPlaystate(PlayState.PAUSED);
					
					window.setBackEnabled(false);
					window.setPaused(true);
				}
				if (index >= (timeLine.size() - 1)) {
					index = timeLine.size() - 1;
					setPlaystate(PlayState.PAUSED);

					window.setForwardEnabled(false);
					window.setPaused(true);
				}
				
				worldData.loadSchematic(timeLine.get(index));
				worldController.updateWithNewData();
				
				window.setStep(index);
				
				if (hasDelay)
					wait(100l);
			}
		} catch (InterruptedException e) {
			Log.e("The time controller's thread was rudely abrupted! :o");			
			
		} catch (SchematicException | IOException e) {
			Log.e("Could not read precomputed schematic.");
		}
	}
	
	public void loadCurrentTimeIntoSchematic() {
		try {			
			worldData.loadSchematic(timeLine.get(index));
			simController.setSchematic(worldData);
			worldController.updateWithNewData();
			
		} catch (SchematicException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTimeWindow(TimeWindow window) {
		this.window = window;
	}
}
