package gui.bettergui.windows.world;

import gui.bettergui.InternalWindow;
import gui.bettergui.menu.LinkedCheckbox;
import gui.controllers.MainController;
import gui.controllers.WorldController;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
