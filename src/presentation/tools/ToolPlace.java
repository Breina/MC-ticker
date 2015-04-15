package presentation.tools;

import logging.Log;
import presentation.controllers.BlockController;
import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.main.Cord3S;
import presentation.objects.Block;

import java.awt.event.MouseEvent;

public class ToolPlace extends Tool {
	
	private char dragBlock;
    private boolean dragging;
    private boolean doUpdate;

	public ToolPlace(MainController mainController) {
		super(mainController, "Place", "block.png", false);
        dragging = false;
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
				dragBlock = Block.BLOCK_AIR;
		}

        int modifiers = e.getModifiersEx();
        doUpdate = !((MouseEvent.SHIFT_DOWN_MASK & modifiers) == MouseEvent.SHIFT_DOWN_MASK);
        dragging = true;

		setBlock();
	}
	
	private void setBlock() {
		Cord3S c = getSelectedCord3D();

		if (c == null) {
            Log.e("Selected cord is null!");
            return;
        }

        WorldController worldController = getWorldController();

        // Temp set it in worldData so we can rotate it properly first
        worldController.getWorldData().setBlock(c.x, c.y, c.z, dragBlock);

        BlockController blockController = mainController.getBlockController();

        byte blockId = Block.getId(dragBlock);

        if (blockId != 0 && blockController.getBlock(blockId).isSideDependent())
        // Find best rotation
            ToolRotate.rotateUntilValid(worldController, mainController.getBlockController(), c, true);
        else
            worldController.setBlock(c.x, c.y, c.z, dragBlock, doUpdate);
    }

	@Override
	public void mouseReleased(MouseEvent e) {
        dragging = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void onSelectionChanged() {
		if (dragging)
            setBlock();
	}

}
