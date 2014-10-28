package presentation.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import presentation.exceptions.SchematicException;
import presentation.gui.time.PlayState;
import presentation.gui.time.TimeLine;
import presentation.gui.windows.world.TimeWindow;
import presentation.objects.ViewData;
import logging.Log;
import sim.objects.WorldState;


public class TimeController implements Runnable {
	
	private TimeLine<WorldState> timeLine; 
	private int tickCounter = 0;
	private int maxCount = 0;
	
	private WorldController worldController;
	private SimController simController;
	
	private ViewData viewData;
	
//	private HashSet<Integer> foundHashes;
//	private boolean checkHash;
	
	private boolean goForward, isPaused, hasDelay;
	
	private TimeWindow window;
	
	private boolean go;
	
	public TimeController(WorldController worldController) {
		
		this.worldController = worldController;
		this.simController = worldController.getSimController();
		
		viewData = worldController.getWorldData();
		
		timeLine = new TimeLine<>(100);
//		foundHashes = new HashSet<>();
		
		setPlaystate(PlayState.PAUSED);
	}
	
	public void init() {
		isPaused = true;
		
		try {
//			Tag schematic = simController.getSchematic(worldData);
			WorldState state = simController.getState();
			
			viewData.setState(state);
			worldController.onSchematicUpdated();
			
			timeLine.init(state);
			
//			foundHashes.clear();
//			foundHashes.add(schematic.hashCode());
//			checkHash = true;
			
			window.setStep(0);
			
			go = true;
			new Thread(this).start();
			
		} catch (SchematicException e) {
			e.printStackTrace();
		}
	}
	
	public void updateCurrentSchematic() {
		
		try {
			WorldState state = simController.getState();
//			foundHashes.clear();
			timeLine.set(state);
			viewData.setState(state);
			worldController.onSchematicUpdated();
			
		} catch (SchematicException e) {
			Log.e("Failed to update updated state: " + e.getMessage());
		}
	}
	
	private WorldState tick() {
		
		simController.tick();
		
//		Tag schematic = simController.getSchematic(worldData);
		WorldState state = simController.getState();

//		if (checkHash && !isPaused) {
//			int hash = schematic.hashCode();
//			
//			if (foundHashes.contains(hash) && !isPaused) {
//				checkHash = false;
//				return null;
//			}
//			
//			foundHashes.add(hash);
//		}
		
		timeLine.add(state);
		
		return state;
	}
	
	public synchronized void setPlaystate(PlayState playState) {
		
		try {
		
			switch (playState) {
				
				case START:
					isPaused = true;
					hasDelay = false;
					
					viewData.setState(timeLine.first());
					worldController.onSchematicUpdated();
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
//					checkHash = true;
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
					
					viewData.setState(timeLine.last());
					worldController.onSchematicUpdated();
					window.setStep(maxCount);
					window.setBackEnabled(true);
			}
		
		} catch (SchematicException e) {
			Log.e("Could not set playstate " + playState + ": " + e.getMessage());
		}
	}

	@Override
	public synchronized void run() {
		
		// Hardcore thread never ends
		
		try {
			
			WorldState state;
			
			while (go) {
				
				if (isPaused)
					wait();
				
				if (goForward) {
					if (timeLine.atEnd()) {
						state = tick();
						if (state == null) {
							isPaused = true;
							window.setPaused(true);
							continue;
						}
					}
					else
						state = timeLine.next();
					
					tickCounter++;
					if (tickCounter > maxCount)
						maxCount = tickCounter;
					
					window.setBackEnabled(true);
					
				} else {
//					checkHash = true;
					if (timeLine.atStart()) {
						state = timeLine.first();
						setPlaystate(PlayState.PAUSED);
						
					} else {
						state = timeLine.prev();
						
						if (timeLine.atStart()) {
							setPlaystate(PlayState.PAUSED);
							window.setPaused(true);
							window.setBackEnabled(false);
						}
						
						tickCounter--;
					}
				}
						
					
				viewData.setState(state);
				worldController.onSchematicUpdated();
				
				window.setStep(tickCounter);
				
				if (hasDelay)
					wait(90l);
				
				wait(10l);
			}
		} catch (InterruptedException e) {
			Log.e("The time controller's thread was rudely abrupted! :o");			
			
		} catch (SchematicException e) {
			Log.e("Could not read precomputed schematic.");
		}
	}
	
	public void loadCurrentTimeIntoSchematic(boolean ignoreIfAtEnd) {
		if (ignoreIfAtEnd && timeLine.atEnd())
			return;

		simController.setState(timeLine.get());
	}
	
	public void setTimeWindow(TimeWindow window) {
		this.window = window;
	}
	
	public void stopThread() {
		go = false;
	}
}
