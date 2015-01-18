package presentation.gui;

import presentation.controllers.WorldController;

public interface WorldListener {

    public void onWorldAdded(WorldController worldController);
    public void onWorldRemoved(WorldController worldController);
}
