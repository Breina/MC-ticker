package presentation.gui.windows.world;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import presentation.InternalWindow;
import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.gui.menu.LinkedCheckbox;

public class WorldWindow extends InternalWindow {
	
	private WorldController controller;
	private LinkedCheckbox checkbox;

	public WorldWindow(WorldController controller, String title, boolean visibleByDefault) {
		super(controller.getMainController(), title);
		
		this.controller = controller;
		
		checkbox = new LinkedCheckbox(this, visibleByDefault);
		setVisible(visibleByDefault);
		
		controller.getWorldMenu().addLinkedCheckbox(checkbox);

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
