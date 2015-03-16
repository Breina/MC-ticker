package presentation.gui.windows.world;

import presentation.controllers.WorldController;
import presentation.gui.menu.LinkedCheckbox;
import presentation.gui.windows.InternalWindow;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class WorldWindow extends InternalWindow {
	
	final WorldController controller;
	private final LinkedCheckbox checkbox;

	WorldWindow(JDesktopPane parent, WorldController controller, String title, boolean visibleByDefault) {
        super(parent, title, visibleByDefault);
		
		this.controller = controller;
		
		checkbox = new LinkedCheckbox(this, visibleByDefault);
		setVisible(visibleByDefault);
		
		controller.getWorldMenu().addLinkedCheckbox(checkbox);

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addComponentListener(new CloseHandler());
	}
	
	private class CloseHandler extends ComponentAdapter {
		@Override
		public void componentHidden(ComponentEvent e) {
			
			checkbox.setSelected(false);
		}
	}
}
