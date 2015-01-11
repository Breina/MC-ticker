package presentation.gui.windows.main;

import presentation.controllers.MainController;
import presentation.gui.InternalWindow;
import presentation.gui.menu.LinkedCheckbox;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainWindow extends InternalWindow {
	private static final long serialVersionUID = -7291404124042489301L;
	
	private MainController controller;
	private LinkedCheckbox checkbox;

	public MainWindow(MainController controller, String title, boolean visibleByDefault) {
		super(controller, title, visibleByDefault);
		
		this.controller = controller;
		
		checkbox = new LinkedCheckbox(this, visibleByDefault);
		setVisible(visibleByDefault);
		
		controller.getWindowMenu().addLinkedCheckbox(checkbox);

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addComponentListener(new CloseHandler());
	}
	
	protected class CloseHandler extends ComponentAdapter {
		@Override
		public void componentHidden(ComponentEvent e) {
			
			checkbox.setSelected(false);
		}
	}
}
