package presentation.tools;

import java.awt.event.MouseEvent;

import presentation.controllers.BlockController;
import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.main.Cord3S;
import presentation.objects.Block;

public class ToolPlace extends Tool {
	
	private Block dragBlock;

	public ToolPlace(MainController mainController) {
		super(mainController, "Place", "block.png", false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		switch (e.getButton()) {
			case MouseEvent.BUTTON3:
				dragBlock = mainController.getBlock();
				break;
				
			default:
			case MouseEvent.BUTTON1:
				dragBlock = Block.B_AIR;
		}
		
		setBlock();
	}
	
	private void setBlock() {
		Cord3S c = getSelectionCord();
//		WorldController worldController = getWorldController();
		
		getWorldController().setBlock(c.x, c.y, c.z, dragBlock);
		
//		worldController.getTimeController().loadCurrentTimeIntoSchematic(true);
//		worldController.getSimController().setBlock(c.x, c.y, c.z, dragBlock.getId(), dragBlock.getData());
//		worldController.getTimeController().updateCurrentSchematic();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragBlock = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectionChanged() {
		if (dragBlock != null)
			setBlock();
	}

}
