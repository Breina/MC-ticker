package presentation.tools;

import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.main.Cord2S;
import presentation.main.Cord3S;

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

    protected Cord2S getSelectedCord2D() {
        return mainController.getSelectedCord2D();
    }
	
	protected Cord3S getSelectionCord3D() {
		return mainController.getSelectedCord3D();
	}

	// TODO find a fix for when there is no world
	protected WorldController getWorldController() {
		return mainController.getSelectedWorld();
	}
}
