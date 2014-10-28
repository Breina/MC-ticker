package presentation.controllers;

import presentation.gui.windows.world.NBTviewer;
import utils.Tag;

public class NBTController {
	
	private SimController controller;
	private NBTviewer nBTViewer;
	
	public NBTController(WorldController worldController, NBTviewer nBTViewer) {
		
		this.controller = worldController.getSimController();
		this.nBTViewer = nBTViewer;
	}
	
	public void onSchematicUpdated() {
		
		Tag tag = controller.getSchematic();
		
		nBTViewer.updateNBTContents(tag);
	}

}
