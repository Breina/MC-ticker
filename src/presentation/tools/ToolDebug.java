package presentation.tools;

import presentation.controllers.MainController;
import presentation.main.Cord3S;

import java.awt.event.MouseEvent;

public class ToolDebug extends Tool {

	public ToolDebug(MainController mainController) {
		super(mainController, "Place", "toolbox.png", false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Cord3S c = getSelectionCord();

		getWorldController().debug(c.x, c.y, c.z);
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void onSelectionChanged() {

	}

}
