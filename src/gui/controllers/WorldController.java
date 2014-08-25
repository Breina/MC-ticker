package gui.controllers;

import gui.bettergui.editor.EditorPanel;
import gui.bettergui.menu.WorldMenu;
import gui.bettergui.windows.world.DrawingWindow;
import gui.bettergui.windows.world.TimeWindow;
import gui.exceptions.SchematicException;
import gui.main.Cord3S;
import gui.objects.Block;
import gui.objects.Orientation;
import gui.objects.ViewData;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import sim.logic.SimWorld;
import utils.Tag;
import logging.Log;

/**
 * Controlls everything about one world
 */
public class WorldController {
	
	private ViewData viewData;
	
	private List<DrawingWindow> windows;
	private WorldMenu worldMenu;
	private TimeWindow time;
	private TimeController timeController;
	
	private MainController mainController;
	private SimController simController;
	
//	private Cord3S selection;
	
	public WorldController(MainController mainController, SimWorld simWorld, File schematicFile) throws SchematicException, IOException, NoSuchAlgorithmException {
		
		this.mainController = mainController;
		this.simController = new SimController(simWorld);
		
		Tag schematic = Tag.readFrom(new FileInputStream(schematicFile));
		
		viewData = new ViewData(schematicFile.getName(),
								(short) schematic.findTagByName("Width").getValue(),
								(short) schematic.findTagByName("Height").getValue(),
								(short) schematic.findTagByName("Length").getValue());
		worldMenu = new WorldMenu(this);
		timeController = new TimeController(this);
		time = new TimeWindow(this);
		
		timeController.setTimeWindow(time);
		
		mainController.getWindowMenu().addWorldMenu(worldMenu);
		
		// TODO new controls window
		windows = new ArrayList<DrawingWindow>();		
		addNewPerspective(Orientation.TOP);
		
		simController.setSchematic(schematic);
		
		timeController.init();
	}
	
	public void addNewPerspective(Orientation orientation) {
		
		DrawingWindow drawingWindow = new DrawingWindow(this, orientation);
		EditorPanel ep = drawingWindow.getEditor();
		
		for (DrawingWindow dw : windows) {
			
			ep.addLayer(dw.getEditor());
			dw.getEditor().addLayer(ep);	
		}
		
		windows.add(drawingWindow);
	}
	
	public void drawingWindowClosed(DrawingWindow source) {
		
//		for (DrawingWindow dw : windows)
//			dw.getEditor().removeLayer(source.getEditor());
		
		windows.remove(source);
		
		if (windows.isEmpty())
			close();
	}
	
	public void close() {
		
		for (DrawingWindow drawingWindow : windows)
			drawingWindow.dispose();
		
		time.dispose();
		timeController.stopThread();
		
		mainController.getWindowMenu().removeWorldMenu(worldMenu);
		mainController.onWorldRemoved(this);
	}
	
	// TODO remove
//	public void onEditorClicked(int button) {
//		
//		Cord3S selection = mainController.getSelectedCord();
//		
//		try {
//			switch (button) {			
//				case MouseEvent.BUTTON1:
//					setBlock(selection.x, selection.y, selection.z, Block.B_AIR);
//					break;
//					
//				case MouseEvent.BUTTON3:
//					setBlock(selection.x, selection.y, selection.z, mainController.getBlock());
//					break;
//			}
//		} catch (NullPointerException e) {
//			Log.e("Setting the block failed, I don't know why this happens sometimes. Try again and good luck next time :)");
//			e.printStackTrace();
//		}
//	}
	
	public void setBlock(final int x, final int y, final int z, final Block block) {
		
		timeController.loadCurrentTimeIntoSchematic(true);
		
		simController.setBlock(x, y, z, block.getId(), block.getData());
		
		timeController.updateCurrentSchematic();
	}
	
	public void updateWithNewData() {
		
		for (DrawingWindow window : windows)
			window.getEditor().updateWithNewData();
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
	
	public TimeWindow getTimeWindow() {
		return time;
	}
	
	public List<DrawingWindow> getOpenWindows() {
		return windows;
	}

	public void revert() {
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
			
			EditorPanel ep = dw.getEditor();
			
			if (!ep.equals(source)) {
				
				ep.unSelect();
			}
		}
		
	}
	
	public void onSelectionUpdated(Cord3S cord, EditorPanel source) {
		
//		selection = cord;
		mainController.onSelectionUpdated(this, cord);
		
		if (source != null)
			for (DrawingWindow dw : windows) {
				
				EditorPanel ep = dw.getEditor();
				
				if (!ep.equals(source)) {
					
					ep.selectCord(cord);
				}
			}
	}
	
	public void updateLayers(EditorPanel source) {
		
		for (DrawingWindow dw : windows) {
			
			EditorPanel ep = dw.getEditor();
			
			if (ep.getOrientation() == source.getOrientation())
				continue;
			
			if (!ep.equals(source)) {
				
				ep.updateLayer(source);
			}
		}
	}
	
	@Override
	public String toString() {
		return viewData.getName();
	}
	
	public SimController getSimController() {
		return simController;
	}
}
