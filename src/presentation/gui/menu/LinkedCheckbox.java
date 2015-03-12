package presentation.gui.menu;

import presentation.gui.InternalWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LinkedCheckbox extends JCheckBox {
	private static final long serialVersionUID = 8837610138802553238L;
	
	private final InternalWindow window;
	
	public LinkedCheckbox(InternalWindow window, boolean defaultState) {
		super(window.getTitle(), defaultState);
		
		this.window = window;
		
		addActionListener(new ToggleHandler());
	}
	
	private class ToggleHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			window.setVisible(isSelected());
		}
	}
}
