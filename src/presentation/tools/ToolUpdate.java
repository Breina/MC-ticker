package presentation.tools;

import presentation.controllers.MainController;
import presentation.main.Cord3S;

import java.awt.event.MouseEvent;

public class ToolUpdate extends Tool {

	public ToolUpdate(MainController mainController) {
		super(mainController, "Update", "update.png", false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Cord3S c = getSelectedCord3D();
        boolean shift = (MouseEvent.SHIFT_DOWN_MASK & e.getModifiersEx()) == MouseEvent.SHIFT_DOWN_MASK;
		getWorldController().getSimController().updateBlock(c.x, c.y, c.z, shift);
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
