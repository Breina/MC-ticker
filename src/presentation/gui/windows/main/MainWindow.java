package presentation.gui.windows.main;

import presentation.controllers.MainController;
import presentation.gui.menu.LinkedCheckbox;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainWindow extends JInternalFrame {
	private static final long serialVersionUID = -7291404124042489301L;
	
	private final MainController controller;
	private final LinkedCheckbox checkbox;

	MainWindow(JComponent parent, MainController controller, String title, boolean visibleByDefault) {
		super(title);
		
		this.controller = controller;
		
		checkbox = new LinkedCheckbox(this, visibleByDefault);
		setVisible(visibleByDefault);
		
		controller.getFrame().getWindowMenu().addLinkedCheckbox(checkbox);

		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addComponentListener(new CloseHandler());

        parent.add(this);
        setVisible(visibleByDefault);
	}
	
	private class CloseHandler extends ComponentAdapter {
		@Override
		public void componentHidden(ComponentEvent e) {
			
			checkbox.setSelected(false);
		}
	}
}
