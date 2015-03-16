package presentation.gui.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LinkedCheckbox extends JCheckBox {
	private static final long serialVersionUID = 8837610138802553238L;
	
	private final JInternalFrame window;
	
	public LinkedCheckbox(JInternalFrame window, boolean defaultState) {
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
