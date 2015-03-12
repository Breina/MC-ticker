package presentation.controllers;

import logging.Log;
import presentation.gui.choosers.SchematicChooser;
import presentation.gui.editor.Editor;
import presentation.gui.editor.entity.EntityManager;
import presentation.gui.editor.layer.LayerManager;
import presentation.gui.editor.selection.SelectionManager;
import presentation.gui.menu.WorldMenu;
import presentation.gui.windows.world.DrawingWindow;
import presentation.gui.windows.world.NBTviewer;
import presentation.main.Constants;
import presentation.main.Cord2S;
import presentation.main.Cord3S;
import presentation.objects.Block;
import presentation.objects.Orientation;
import presentation.objects.ViewData;
import sim.logic.SimWorld;
import utils.Tag;

import javax.swing.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controlls everything about one world
 */
public class WorldController {
	
	private ViewData viewData;
	
	private CopyOnWriteArrayList<Editor> editors;
	private WorldMenu worldMenu;
	private TimeController timeController;
	
	private final MainController mainController;
	private final SimController simController;
	
	private NBTviewer nbtViewer;
	private NBTController nbtController;

	private File lastSavedFile;

    private LayerManager layerManager;
    private EntityManager entityManager;
    private SelectionManager selectionManager;

    private long lastUpdateTime;
    private boolean doUpdate;
    private TimerUpdater timerUpdater;

    public WorldController(MainController mainController, SimWorld simWorld, String name, short xSize, short ySize, short zSize) {

		this.mainController = mainController;
		this.simController = new SimController(simWorld);

		viewData = new ViewData(name, xSize, ySize, zSize);
		simController.createNewWorld(xSize, ySize, zSize);

		loadSim();
	}
	
	public WorldController(MainController mainController, SimWorld simWorld, File schematicFile) throws IOException, NoSuchAlgorithmException {
		
		this.mainController = mainController;
		this.simController = new SimController(simWorld);
		this.lastSavedFile = schematicFile;
		
		Tag schematic = Tag.readFrom(new FileInputStream(schematicFile));
		
		viewData = new ViewData(schematicFile.getName(),
								(short) schematic.findTagByName("Width").getValue(),
								(short) schematic.findTagByName("Height").getValue(),
								(short) schematic.findTagByName("Length").getValue());

		// Loads the world
		simController.setSchematic(schematic);

		loadSim();
	}

	private void loadSim() {

        timerUpdater = new TimerUpdater();
        Thread timerUpdaterThread = new Thread(timerUpdater);
        timerUpdaterThread.start();

		worldMenu = new WorldMenu(this);
		timeController = new TimeController(this);

		nbtViewer = new NBTviewer(mainController.getFrame().getDesktop(), this);
		nbtController = new NBTController(this, nbtViewer);

        layerManager = new LayerManager(this);
        entityManager = new EntityManager(this);
        selectionManager = new SelectionManager(this);

        // Adds the world to the menu
        mainController.getFrame().getWindowMenu().addWorldMenu(worldMenu);

        // This will fill ViewData
        timeController.init();

        editors = new CopyOnWriteArrayList<>();
        addNewPerspective(Orientation.TOP);

        setDoUpdate(true);
	}
	
	public void addNewPerspective(Orientation orientation) {
		
		DrawingWindow drawingWindow = new DrawingWindow(mainController.getFrame().getDesktop(), this, orientation);
        Editor editor = drawingWindow.getEditor();

        layerManager.addLayer(editor);
        entityManager.addEditor(editor);

        editors.add(editor);
	}
	
	public void drawingWindowClosed(DrawingWindow source) {

		layerManager.removeLayer(source.getEditor());
		
		editors.remove(source.getEditor());
		
		if (editors.isEmpty())
			close();
	}
	
	public void close() {

        // I'm so sorry :(
        for (Editor editor : editors) ((JInternalFrame) editor.getParent().getParent().getParent()).dispose();

		timeController.stopThread();
		
		nbtViewer.dispose();
		
		mainController.getFrame().getWindowMenu().removeWorldMenu(worldMenu);
		mainController.onWorldRemoved(this);
	}
	
	public void setBlock(final int x, final int y, final int z, final Block block) {

        setDoUpdate(false);
		
		timeController.loadCurrentTimeIntoSchematic(true);
		
		simController.setBlock(x, y, z, block.getId(), block.getData());
		
		timeController.updateCurrentSchematic();

        setDoUpdate(true);
	}
	
	public void onSchematicUpdated() {

        if (!shouldUpdate())
            return;

        doUpdate = false;

		nbtController.onSchematicUpdated();

        entityManager.updateEntities();

        for (Editor editor : editors)
            editor.onSchematicUpdated();

        doUpdate = true;
	}
	
	public ViewData getWorldData() {
		return viewData;
	}
	
	public MainController getMainController() {
		return mainController;
	}
	
	public TimeController getTimeController() {
		return timeController;
	}
	
	public WorldMenu getWorldMenu() {
		return worldMenu;
	}
	
	public List<Editor> getEditors() {
		return editors;
	}

	private int updateSavedFile() {

		SchematicChooser chooser = new SchematicChooser(new File("schems"));
		chooser.setSelectedFile(new File(getWorldData().getName() + ".schematic"));

		int result = chooser.showOpenDialog(mainController.getFrame());

		if (result == SchematicChooser.APPROVE_OPTION)
			lastSavedFile = chooser.getSelectedFile();

		return result;
	}

	public void save() {

		if (lastSavedFile == null)
			if (updateSavedFile() != SchematicChooser.APPROVE_OPTION)
				return;

		try {
			simController.saveWorld(new FileOutputStream(lastSavedFile));

		} catch (FileNotFoundException e) {

			Log.e("Failed to save world: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void saveAs() {

		if (updateSavedFile() != SchematicChooser.APPROVE_OPTION)
			return;

		try {
			simController.saveWorld(new FileOutputStream(lastSavedFile));

		} catch (FileNotFoundException e) {

			Log.e("Failed to save world: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void revert() {

		if (lastSavedFile == null) {

			Log.e("No file to revert from");
			return;
		}

		try {
			simController.setSchematic(Tag.readFrom(new FileInputStream(lastSavedFile)));

			timeController.init();
			onSchematicUpdated();

		} catch (IOException | NoSuchAlgorithmException e) {

			Log.e("Failed to revert to file: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void onSelectionUpdated(Cord2S cord2D, Cord3S cord3D, Editor source) {

		mainController.onSelectionUpdated(this, cord2D, cord3D, true);
		
		if (source != null)
			for (Editor editor : editors) {
				
				if (!editor.equals(source)) {
					
					editor.selectCord(cord3D);
				}
			}
	}

	public void debug(int x, int y, int z) {

		simController.debug(x, y, z);
	}

	@Override
	public String toString() {
		return viewData.getName();
	}
	
	public SimController getSimController() {
		return simController;
	}

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public void setDoUpdate(boolean doUpdate)  {

        this.doUpdate = doUpdate;

        if (doUpdate)
            onSchematicUpdated();
    }

    synchronized boolean shouldUpdate() {

        if (!doUpdate)
            return false;

        long newTime = System.currentTimeMillis();
        boolean result = newTime - lastUpdateTime >= Constants.MIN_FRAME_DELAY;

        if (result)
            lastUpdateTime = newTime;

        timerUpdater.setTimeTarget(newTime + Constants.MIN_FRAME_DELAY);

        return result;
    }

    class TimerUpdater implements Runnable {

        private long timeTarget;

        @Override
        public void run() {

            for (;;) {
                try {

                    while (System.currentTimeMillis() < timeTarget)
                            Thread.sleep(Constants.MIN_FRAME_DELAY);

                    onSchematicUpdated();

                    synchronized (this) {
                        wait();
                    }

                } catch (InterruptedException e) {
                    Log.w("Frame updater thread was awoken from its sleep.");
                }
            }
        }

        public synchronized void setTimeTarget(long timeTarget) {
            this.timeTarget = timeTarget;
            notify();
        }
    }
}
