package presentation.tools;

import java.awt.event.MouseEvent;

import presentation.blocks.BlockLogic;
import presentation.controllers.BlockController;
import presentation.controllers.MainController;
import presentation.main.Cord3S;
import presentation.objects.Block;

public class ToolRotate extends Tool {
	
	private int dragButton;
	private BlockController blockController;

	public ToolRotate(MainController mainController) {
		super(mainController, "Rotate", "rotate.png", false);
		
		this.blockController = mainController.getBlockController();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dragButton = e.getButton();
		rotate();
	}
	
	private void rotate() {
		Cord3S c = getSelectionCord();
		Block block = getWorldController().getWorldData().getBlock(c.x, c.y, c.z);
		
		BlockLogic blockLogic = blockController.getBlock(block.getId());
		if (blockLogic == null)
			return;
		
		block.setData(blockLogic.rotate(block.getData(), dragButton == MouseEvent.BUTTON1));
		getWorldController().setBlock(c.x, c.y, c.z, block);
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
			rotate();
	}

}