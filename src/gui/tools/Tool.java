package gui.tools;

import gui.controllers.MainController;
import gui.controllers.WorldController;
import gui.main.Cord3S;

import java.awt.event.MouseListener;

public abstract class Tool implements MouseListener {
	
	protected MainController mainController;
	
	private boolean hasMouseMotionListener;
	private String name, fileName;
	
	public Tool(MainController mainController, String name, boolean usesMouseMotion) {
		this(mainController, name, null, usesMouseMotion);
	}
	
	public Tool(MainController mainController, String name, String fileName, boolean usesMouseMotion) {
		this.mainController = mainController;
		this.name = name;
		this.fileName = fileName;
		
		this.hasMouseMotionListener = usesMouseMotion;
	}
	
	public abstract void onSelectionChanged();
	
	public boolean hasMouseMotionListener() {
		return hasMouseMotionListener;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	protected Cord3S getSelectionCord() {
		return mainController.getSelectedCord();
	}
	
	protected WorldController getWorldController() {
		return mainController.getSelectedWorld();
	}
}
