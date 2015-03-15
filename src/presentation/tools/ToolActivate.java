package presentation.tools;

import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.main.Cord3S;

import java.awt.event.MouseEvent;

public class ToolActivate extends Tool {
	
	private int dragButton;
	private final MainController mainController;

	public ToolActivate(MainController mainController) {
		super(mainController, "Activate", "cursor.png", false);

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
		Cord3S c = getSelectedCord3D();

		WorldController worldController = mainController.getSelectedWorld();
		worldController.getSimController().activateBlock(c.x, c.y, c.z);
		
		worldController.getTimeController().updateCurrentSchematic();

        worldController.getMainController().onSelectionUpdated(getWorldController(), getSelectedCord2D(), c, false);
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
