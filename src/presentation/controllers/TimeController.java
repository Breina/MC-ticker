package presentation.controllers;

import logging.Log;
import presentation.gui.time.PlayState;
import presentation.gui.time.TimeInfo;
import presentation.gui.time.TimeLine;
import presentation.objects.ViewData;
import utils.Tag;

public class TimeController {
	
	private final TimeLine<Tag> timeLine;

	private int tickCounter = 0;
    private int minCount = 0;
	private int maxCount = 0;
	
	private final WorldController worldController;
	private final SimController simController;
	
	private final ViewData viewData;

	private final TimeInfo timeInfo;

	private boolean goForward, isPaused, hasDelay;
	private boolean go;
    private Thread thready;

    public TimeController(WorldController worldController) {

		this.worldController = worldController;
		this.simController = worldController.getSimController();

		timeInfo = worldController.getMainController().getFrame().getTimebar();
		viewData = worldController.getWorldData();
		
		timeLine = new TimeLine<>(sim.constants.Constants.TIMELINE_LENGTH);
		
		setPlaystate(PlayState.PAUSED);
	}

	public void init() {

		isPaused = true;

		viewData.setState(simController.getBlocks(), simController.getEntityObjects());
		timeLine.init(simController.getSchematic());

		timeInfo.setStep(0);
		go = true;
        (thready = new Thready()).start();
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
				viewData.setState(simController.getBlocks(), simController.getEntityObjects());

				worldController.onSchematicUpdated();
                setTickCounter(minCount);
				break;

			case RUSHBACK:
				isPaused = false;
				goForward = false;
				hasDelay = false;

                notifyThready();
				break;

			case PLAYBACK:
				isPaused = false;
				goForward = false;
				hasDelay = true;

                notifyThready();
				break;

			case STEPBACK:
				isPaused = true;
				goForward = false;
				hasDelay = false;

                notifyThready();
				break;

			case PAUSED:
				isPaused = true;
				break;

			case STEPFORWARD:
				isPaused = true;
				goForward = true;
				hasDelay = false;

                notifyThready();
				break;

			case PLAYFORWARD:
				isPaused = false;
				goForward = true;
				hasDelay = true;

                notifyThready();
				break;

			case RUSHFORWARD:
				isPaused = false;
				goForward = true;
				hasDelay = false;

                notifyThready();
				break;

			case END:
				isPaused = true;
				hasDelay = false;

				// TODO setSchematic may not be necessary
				simController.setSchematic(timeLine.last());
				viewData.setState(simController.getBlocks(), simController.getEntityObjects());

				worldController.onSchematicUpdated();
                setTickCounter(maxCount);
                timeInfo.setBackEnabled(true);
		}
	}

    private synchronized void notifyThready() {
        synchronized (thready) {
            thready.notify();
        }
    }

    private void setTickCounter(int tickCounter) {
        timeInfo.setStep(tickCounter);

        if (tickCounter > maxCount)
            this.maxCount = tickCounter;

        if (tickCounter - minCount > sim.constants.Constants.TIMELINE_LENGTH)
            this.minCount = tickCounter - sim.constants.Constants.TIMELINE_LENGTH;

        this.tickCounter = tickCounter;
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
		viewData.setState(simController.getBlocks(), simController.getEntityObjects());

		worldController.onSchematicUpdated();
	}
	
	public void stopThread() {
		go = false;
	}

	public int getTickCount() {
		return tickCounter;
	}

    public int getTickStartRange() {
        return minCount;
    }

    public int getTickEndRange() {
        return maxCount;
    }

    public void gotoTickCount(int count) {

        if (count < getTickStartRange() || count > getTickEndRange()) {
            Log.e("Tickcount out of buffer: " + count);
            return;
        }

        simController.setSchematic(timeLine.getRelative(count - tickCounter));
        viewData.setState(simController.getBlocks(), simController.getEntityObjects());
    }

    private class Thready extends Thread {

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

                        setTickCounter(tickCounter + 1);
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

                            setTickCounter(tickCounter - 1);
                        }

                        simController.setSchematic(schem);
                    }

                    viewData.setState(simController.getBlocks(), simController.getEntityObjects());
                    worldController.onSchematicUpdated();

                    if (hasDelay)
                        wait(100l);
                    else
                        // If we wouldn't give away our locks for at least a millisecond, shit will freeze.
                        wait(1l);
                }
            } catch (InterruptedException e) {
                Log.e("The time controller's thread was rudely abrupted! :o");

            }
        }
    }
}
