package presentation.controllers;

import logging.Log;
import presentation.exceptions.SchematicException;
import presentation.gui.choosers.SchematicChooser;
import presentation.gui.editor.EditorPanel;
import presentation.gui.editor.IEditor;
import presentation.gui.menu.WorldMenu;
import presentation.gui.windows.world.DrawingWindow;
import presentation.gui.windows.world.NBTviewer;
import presentation.main.Cord3S;
import presentation.objects.Block;
import presentation.objects.Orientation;
import presentation.objects.ViewData;
import sim.logic.SimWorld;
import utils.Tag;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controlls everything about one world
 */
public class WorldController {
	
	private ViewData viewData;
	
	private CopyOnWriteArrayList<DrawingWindow> windows;
	private WorldMenu worldMenu;
	private TimeController timeController;
	
	private MainController mainController;
	private SimController simController;
	
	private NBTviewer nbtViewer;
	private NBTController nbtController;

	private File lastSavedFile;

	public WorldController(MainController mainController, SimWorld simWorld, String name, short xSize, short ySize, short zSize) {

		this.mainController = mainController;
		this.simController = new SimController(simWorld);

		viewData = new ViewData(name, xSize, ySize, zSize);
		simController.createNewWorld(xSize, ySize, zSize);

		loadSim();
	}
	
	public WorldController(MainController mainController, SimWorld simWorld, File schematicFile) throws SchematicException, IOException, NoSuchAlgorithmException {
		
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

		worldMenu = new WorldMenu(this);
		timeController = new TimeController(this);

		nbtViewer = new NBTviewer(this);
		nbtController = new NBTController(this, nbtViewer);

		// Adds the world to the menu
		mainController.getWindowMenu().addWorldMenu(worldMenu);

		// This will fill ViewData
		timeController.init();

		windows = new CopyOnWriteArrayList<>();
		addNewPerspective(Orientation.TOP);
	}
	
	public void addNewPerspective(Orientation orientation) {
		
		DrawingWindow drawingWindow = new DrawingWindow(this, orientation);
		IEditor ep = drawingWindow.getEditor();
		
		for (DrawingWindow dw : windows) {
			
			ep.addLayer(dw.getEditor());
			dw.getEditor().addLayer(ep);	
		}

		windows.add(drawingWindow);

//		if (viewData.getEntities() != null)
//			ep.updateEntities(viewData.getEntities());
	}
	
	public void drawingWindowClosed(DrawingWindow source) {

		Iterator<DrawingWindow> drawingWindowIterator = windows.iterator();

		while (drawingWindowIterator.hasNext())
			drawingWindowIterator.next().getEditor().removeLayer(source.getEditor());

		
		windows.remove(source);
		
		if (windows.isEmpty())
			close();
	}
	
	public void close() {

		Iterator<DrawingWindow> drawingWindowIterator = windows.iterator();

		while (drawingWindowIterator.hasNext()) {
			drawingWindowIterator.next().dispose();
		}

		timeController.stopThread();
		
		nbtViewer.dispose();
		
		mainController.getWindowMenu().removeWorldMenu(worldMenu);
		mainController.onWorldRemoved(this);
	}
	
	public void setBlock(final int x, final int y, final int z, final Block block) {
		
		timeController.loadCurrentTimeIntoSchematic(true);
		
		simController.setBlock(x, y, z, block.getId(), block.getData());
		
		timeController.updateCurrentSchematic();
	}
	
	public void onSchematicUpdated() {
		nbtController.onSchematicUpdated();
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
	
	public List<DrawingWindow> getOpenWindows() {
		return windows;
	}

	private int updateSavedFile() {

		SchematicChooser chooser = new SchematicChooser(new File("schems"));
		chooser.setSelectedFile(new File(getWorldData().getName() + ".schematic"));

		int result = chooser.showOpenDialog(mainController.getRSframe());

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

		// TODO
//		worldData.load();
//		updateWithNewData();
//		timeController.init();
//		
//		destroySim();
//		simController.setSchematic(worldData);
	}
	
	public void unSelectAll(EditorPanel source) {
		
		for (DrawingWindow dw : windows) {
			
			IEditor ep = dw.getEditor();
			
			if (!ep.equals(source)) {
				
				ep.unselect();
			}
		}
	}
	
	public void onSelectionUpdated(Cord3S cord, EditorPanel source) {

		mainController.onSelectionUpdated(this, cord, true);
		
		if (source != null)
			for (DrawingWindow dw : windows) {
				
				IEditor ep = dw.getEditor();
				
				if (!ep.equals(source)) {
					
					ep.selectCord(cord);
				}
			}
	}

	public void updateLayers(IEditor source) {
		
		for (DrawingWindow dw : windows) {
			
			IEditor ep = dw.getEditor();
			
			if (ep.getOrientation() == source.getOrientation())
				continue;
			
			if (!ep.equals(source)) {
				
				ep.updateLayer(source);
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
}
