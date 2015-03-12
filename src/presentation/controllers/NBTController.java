package presentation.controllers;

import presentation.gui.windows.world.NBTviewer;
import utils.Tag;

class NBTController {
	
	private final SimController controller;
	private final NBTviewer nBTViewer;
	
	public NBTController(WorldController worldController, NBTviewer nBTViewer) {
		
		this.controller = worldController.getSimController();
		this.nBTViewer = nBTViewer;
	}
	
	public void onSchematicUpdated() {
		
		Tag tag = controller.getSchematic();
		
		nBTViewer.updateNBTContents(tag);
	}

}
