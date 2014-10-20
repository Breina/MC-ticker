package presentation.tools;

import java.awt.event.MouseEvent;

import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.main.Cord3S;

public class ToolActivate extends Tool {
	
	private int dragButton;
//	private BlockController blockController;
	private MainController mainController;

	public ToolActivate(MainController mainController) {
		super(mainController, "Activate", "cursor.png", false);
		
//		this.blockController = mainController.getBlockController();
		this.mainController = mainController;

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dragButton = e.getButton();
		activate();
	}
	
	private void activate() {
		
		Cord3S c = getSelectionCord();

		WorldController worldController = mainController.getSelectedWorld();
		worldController.getSimController().activateBlock(c.x, c.y, c.z);
		
		worldController.getTimeController().updateCurrentSchematic();
		
//		Cord3S c = getSelectionCord();
//		Block block = getWorldController().getWorldData().getBlock(c.x, c.y, c.z);
//
//		BlockLogic blockLogic = blockController.getBlock(block.getId());
//		if (blockLogic == null)
//			return;
//		
//		block.setData(blockLogic.click(block.getData(), dragButton == MouseEvent.BUTTON1));
//		getWorldController().setBlock(c.x, c.y, c.z, block);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragButton = MouseEvent.NOBUTTON;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void onSelectionChanged() {
		if (dragButton != MouseEvent.NOBUTTON)
			activate();
	}

}
