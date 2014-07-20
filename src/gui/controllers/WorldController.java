package gui.controllers;

import gui.bettergui.DrawingWindow;
import gui.bettergui.TimeWindow;
import gui.bettergui.editor.EditorPanel;
import gui.bettergui.menu.WorldMenu;
import gui.exceptions.SchematicException;
import gui.main.Cord3S;
import gui.objects.Block;
import gui.objects.Orientation;
import gui.objects.WorldData;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import logging.Log;
import utils.CircularByteBuffer;
import utils.Tag;

/**
 * Controlls everything about one world
 */
public class WorldController {
	
	private WorldData worldData;
	
	private List<DrawingWindow> windows;
	private WorldMenu worldMenu;
	private TimeWindow time;
	private TimeController timeController;
	
	private MainController mainController;
	private SimController simController;
	
//	private Cord3S selection;
	
	public WorldController(MainController mainController, File schematicFile) throws SchematicException, IOException, NoSuchAlgorithmException {
		
		this.mainController = mainController;
		this.simController = mainController.getSimController();
		
		worldData = new WorldData(schematicFile);		
		worldMenu = new WorldMenu(this);
		timeController = new TimeController(this);
		time = new TimeWindow(this);
		
		timeController.setTimeWindow(time);
		
		mainController.getWindowMenu().addWorldMenu(worldMenu);
		
		// TODO new controls window
		windows = new ArrayList<DrawingWindow>();		
		addNewPerspective(Orientation.TOP);
		
		simController.setSchematic(worldData);
		
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
		destroySim();
		
		mainController.getWindowMenu().removeWorldMenu(worldMenu);
		mainController.onWorldRemoved(this);
	}
	
	public void onEditorClicked(int button) {
		
		Cord3S selection = mainController.getSelectedCord();
		
		switch (button) {			
			case MouseEvent.BUTTON1:
				setBlock(selection.x, selection.y, selection.z, Block.B_AIR);
				break;
				
			case MouseEvent.BUTTON3:
				setBlock(selection.x, selection.y, selection.z, mainController.getEditorWindow().getSelectedBlock());
				break;
		}
	}
	
	private void setBlock(final int x, final int y, final int z, final Block block) {
		
//		worldData.setBlock(x, y, z, block);
		
		timeController.loadCurrentTimeIntoSchematic();
		
		simController.setBlock(worldData.getName(), x, y, z, block.getId(), block.getData());
				
		timeController.init();
//		
//		try {
//			worldData.loadSchematic(simController.getSchematic(worldData));
//		} catch (SchematicException | IOException e) {
//			e.printStackTrace();
//		}
//		updateWithNewData();
//		
//		System.out.println("stop");
		
	}
	
	private void destroySim() {
		simController.destroy(worldData.getName());
	}
	
	public void tick() {
		
		
		updateWithNewData();
	}
	
	public void updateWithNewData() {
		
		for (DrawingWindow window : windows)
			window.getEditor().updateWithNewData();
	}
	
	public WorldData getWorldData() {
		return worldData;
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

	public void revert() {
		worldData.load();
		updateWithNewData();
		
		destroySim();
		simController.setSchematic(worldData);
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
		return worldData.getName();
	}
}
