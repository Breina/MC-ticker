package gui.bettergui.windows.world;

import gui.bettergui.windows.main.MainWindow;
import gui.controllers.MainController;

import java.awt.Dimension;

import javax.swing.JTextField;

import utils.Tag;

public class NBTviewer extends MainWindow {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private MainController controller;

	public NBTviewer(MainController controller) {
		super(controller, "Editor", false);
		
		this.controller = controller;
		
		buildGUI();
	}
	
	public void buildGUI() {
	}
	
	public void updateNBTContents(Tag schematic) {
		
		
		
	}
}
