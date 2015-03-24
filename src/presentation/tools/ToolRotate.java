package presentation.tools;

import logging.Log;
import presentation.blocks.BlockLogic;
import presentation.controllers.BlockController;
import presentation.controllers.MainController;
import presentation.controllers.SimController;
import presentation.controllers.WorldController;
import presentation.main.Cord3S;
import presentation.objects.Block;

import java.awt.event.MouseEvent;

public class ToolRotate extends Tool {
	
	private int dragButton;
	private final BlockController blockController;
    private final MainController mainController;

	public ToolRotate(MainController mainController) {
		super(mainController, "Rotate", "rotate.png", false);

        this.mainController = mainController;
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

    static void rotateUntilValid(WorldController worldController, BlockController blockController, Cord3S c, boolean forward) {

        Log.d("Rotating...");

        SimController simController = worldController.getSimController();

        Block block = worldController.getWorldData().getBlock(c.x, c.y, c.z);

        BlockLogic blockLogic = blockController.getBlock(block.getId());
        if (blockLogic == null)
            return;

        byte startData = block.getData();
        byte data = startData;
        Object sideBlock;

        do {
            data = blockLogic.rotate(data, forward);

            if (blockLogic.getSide(data) == null)
                break;

            if (data == startData)
                return;

            Cord3S sideCord = blockLogic.getSide(data).add(c);

            sideBlock = simController.getBlockFromState(
                    simController.getBlockState(sideCord.x, sideCord.y, sideCord.z));

        } while (!simController.isFullCube(sideBlock) || !simController.isOpaque(sideBlock));

        block.setData(data);
        worldController.setBlock(c.x, c.y, c.z, block);
    }
	
	private void rotate() {

		Cord3S c = getSelectedCord3D();
        WorldController worldController = getWorldController();

        rotateUntilValid(worldController, blockController, c, true);

//        SimController simController = worldController.getSimController();
//
//		Block block = worldController.getWorldData().getBlock(c.x, c.y, c.z);
//
//		BlockLogic blockLogic = blockController.getBlock(block.getId());
//		if (blockLogic == null)
//			return;
//
//        byte startData = block.getData();
//        byte data = startData;
//        Object sideBlock;
//
//        do {
//            data = blockLogic.rotate(data, dragButton == MouseEvent.BUTTON1);
//
//            if (blockLogic.getSide(data) == null)
//                break;
//
//            if (data == startData)
//                return;
//
//            Cord3S sideCord = blockLogic.getSide(data).add(c);
//
//            sideBlock = simController.getBlockFromState(
//                    simController.getBlockState(sideCord.x, sideCord.y, sideCord.z));
//
//        } while (!simController.isFullCube(sideBlock) || !simController.isOpaque(sideBlock));
//
//		block.setData(data);
//        worldController.setBlock(c.x, c.y, c.z, block);

        mainController.onSelectionUpdated(worldController, getSelectedCord2D(), c, false);
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
