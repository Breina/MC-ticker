package presentation.controllers;

import logging.Log;
import presentation.gui.time.PlayState;
import presentation.gui.time.TimeInfo;
import presentation.gui.time.TimeLine;
import presentation.objects.ViewData;
import utils.Tag;

public class TimeController implements Runnable {
	
	private TimeLine<Tag> timeLine;

	private int tickCounter = 0;
	private int maxCount = 0;
	
	private WorldController worldController;
	private SimController simController;
	
	private ViewData viewData;

	private TimeInfo timeInfo;

	private boolean goForward, isPaused, hasDelay;
	private boolean go;

	public TimeController(WorldController worldController) {

		this.worldController = worldController;
		this.simController = worldController.getSimController();

		timeInfo = worldController.getMainController().getTimebar();
		viewData = worldController.getWorldData();
		
		timeLine = new TimeLine<>(100);
		
		setPlaystate(PlayState.PAUSED);
	}

	public void init() {

		isPaused = true;

		viewData.setState(simController.getBlockObjects(), simController.getEntityObjects());
		timeLine.init(simController.getSchematic());

		timeInfo.setStep(0);
		go = true;
		new Thread(this).start();
	}

	private Tag tick() {

		simController.tick();

		Tag schematic = simController.getSchematic();
		timeLine.add(schematic);
		
		return schematic;
	}
	
	public synchronized void setPlaystate(PlayState playState) {


		switch (playState) {

			case START:
				isPaused = true;
				hasDelay = false;


				simController.setSchematic(timeLine.first());
				viewData.setState(simController.getBlockObjects(), simController.getEntityObjects());

				worldController.onSchematicUpdated();
				timeInfo.setStep(0);
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

				// TODO setSchematic may not be necessary
				simController.setSchematic(timeLine.last());
				viewData.setState(simController.getBlockObjects(), simController.getEntityObjects());

				worldController.onSchematicUpdated();
				timeInfo.setStep(maxCount);
				timeInfo.setBackEnabled(true);
		}
	}

	@Override
	public synchronized void run() {
		
		// Hardcore thread never ends
		
		try {
			
			Tag schem;
			
			while (go) {
				
				if (isPaused)
					wait();
				
				if (goForward) {
					if (timeLine.atEnd())
						schem = tick();

					else {
						schem = timeLine.next();
						simController.setSchematic(schem);
					}
					
					tickCounter++;
					if (tickCounter > maxCount)
						maxCount = tickCounter;

					timeInfo.setBackEnabled(true);
					
				} else {
					if (timeLine.atStart()) {
						schem = timeLine.first();
						setPlaystate(PlayState.PAUSED);
						
					} else {
						schem = timeLine.prev();
						
						if (timeLine.atStart()) {
							setPlaystate(PlayState.PAUSED);
							timeInfo.setPaused(true);
							timeInfo.setBackEnabled(false);
						}
						
						tickCounter--;
					}

					simController.setSchematic(schem);
				}

				viewData.setState(simController.getBlockObjects(), simController.getEntityObjects());
				worldController.onSchematicUpdated();

				timeInfo.setStep(tickCounter);
				
				if (hasDelay)
					wait(90l);
				
				wait(10l);
			}
		} catch (InterruptedException e) {
			Log.e("The time controller's thread was rudely abrupted! :o");			
			
		}
	}

	/**
	 * Going back in time is only updated visually
	 */
	public void loadCurrentTimeIntoSchematic(boolean ignoreIfAtEnd) {

		if (ignoreIfAtEnd && timeLine.atEnd())
			return;

		simController.setSchematic(timeLine.get());
	}

	public void updateCurrentSchematic() {

		Tag schem = simController.getSchematic();

		timeLine.set(schem);
		viewData.setState(simController.getBlockObjects(), simController.getEntityObjects());

		worldController.onSchematicUpdated();
	}
	
	public void stopThread() {
		go = false;
	}

	public int getTickCounter() {
		return tickCounter;
	}
}
