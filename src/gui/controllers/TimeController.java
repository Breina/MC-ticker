package gui.controllers;

import gui.bettergui.TimeWindow;
import gui.bettergui.time.PlayState;
import gui.bettergui.time.TimeLine;
import gui.exceptions.SchematicException;
import gui.objects.WorldData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import logging.Log;
import utils.Tag;


public class TimeController implements Runnable {
	
	private TimeLine timeLine; 
	private int tickCounter = 0;
	private int maxCount = 0;
	
	private WorldController worldController;
	private SimController simController;
	
	private WorldData worldData;
	
//	private HashSet<Integer> foundHashes;
	
	private boolean goForward, isPaused, hasDelay;
	
	private TimeWindow window;
	
	private Thread thread;
	
	public TimeController(WorldController worldController) {
		
		this.worldController = worldController;
		this.simController = worldController.getMainController().getSimController();
		
		worldData = worldController.getWorldData();
		
		timeLine = new TimeLine(100);
//		foundHashes = new HashSet<>();
		
		setPlaystate(PlayState.PAUSED);
	}
	
	public void init() {
		isPaused = true;
		
		try {
			Tag schematic = simController.getSchematic(worldData);
			
			worldData.setSchematic(schematic);
			worldController.updateWithNewData();
			
//			foundHashes.clear();
			timeLine.init(schematic);
			
//			foundHashes.add(schematic.hashCode());
			
			window.setStep(0);
//			window.setEndFound(false);
			
			thread = new Thread(this);
			thread.start();
			
		} catch (SchematicException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateCurrentSchematic(Tag schematic) {
		
		try {
			timeLine.set(schematic);
			worldData.setSchematic(schematic);
			worldController.updateWithNewData();
			
		} catch (SchematicException | IOException e) {
			Log.e("Failed to update updated schematic: " + e.getMessage());
		}
	}
	
	private Tag tick() {
		
		simController.tick(worldData.getName());
		
		Tag schematic = simController.getSchematic(worldData);
		timeLine.add(schematic);
		
		return schematic;
	}
	
	public synchronized void setPlaystate(PlayState playState) {
		
		try {
		
			switch (playState) {
				
				case START:
					isPaused = true;
					hasDelay = false;
					
					worldData.setSchematic(timeLine.first());
					worldController.updateWithNewData();
					window.setStep(0);
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
					
					worldData.setSchematic(timeLine.last());
					worldController.updateWithNewData();
					window.setStep(maxCount);
					window.setBackEnabled(true);
			}
		
		} catch (SchematicException | IOException e) {
			Log.e("Could not set playstate " + playState + ": " + e.getMessage());
		}
	}

	@Override
	public synchronized void run() {
		
		// Hardcore thread never ends
		
		try {
			
			Tag schematic;
			
			for (;;) {
				
				if (isPaused)
					wait();
				
				if (goForward) {
					if (timeLine.atEnd())
						schematic = tick();
					else
						schematic = timeLine.next();
					
					tickCounter++;
					if (tickCounter > maxCount)
						maxCount = tickCounter;
					
					window.setBackEnabled(true);
					
				} else
					if (timeLine.atStart()) {
						schematic = timeLine.first();
						setPlaystate(PlayState.PAUSED);
						
					} else {
						schematic = timeLine.prev();
						
						if (timeLine.atStart()) {
							setPlaystate(PlayState.PAUSED);
							window.setPaused(true);
							window.setBackEnabled(false);
						}
						
						tickCounter--;
					}
						
					
				worldData.setSchematic(schematic);
				worldController.updateWithNewData();
				
				window.setStep(tickCounter);
				
				if (hasDelay)
					wait(90l);
				
				wait(10l);
			}
		} catch (InterruptedException e) {
			Log.e("The time controller's thread was rudely abrupted! :o");			
			
		} catch (SchematicException | IOException e) {
			Log.e("Could not read precomputed schematic.");
		}
	}
	
	public void loadCurrentTimeIntoSchematic() {
		try {
			if (timeLine.atEnd())
				return;
			
			worldData.setSchematic(timeLine.get());
			simController.setSchematic(worldData);
			
		} catch (SchematicException | IOException e) {
			
			Log.e("Could not prepare simulator before placing block:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setTimeWindow(TimeWindow window) {
		this.window = window;
	}
}
