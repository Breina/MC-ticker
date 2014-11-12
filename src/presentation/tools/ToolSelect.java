package presentation.tools;

import presentation.controllers.MainController;

import java.awt.event.MouseEvent;

public class ToolSelect extends Tool {

	public ToolSelect(MainController mainController) {
		super(mainController, "Select", "select.png", false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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
