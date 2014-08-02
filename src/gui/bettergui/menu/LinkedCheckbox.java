package gui.bettergui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.bettergui.InternalWindow;
import gui.bettergui.windows.main.MainWindow;

import javax.swing.JCheckBox;

public class LinkedCheckbox extends JCheckBox {
	private static final long serialVersionUID = 8837610138802553238L;
	
	private InternalWindow window;
	
	public LinkedCheckbox(InternalWindow window, boolean defaultState) {
		super(window.getTitle(), defaultState);
		
		this.window = window;
		
		addActionListener(new ToggleHandler());
	}
	
	protected class ToggleHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			window.setVisible(isSelected());
		}
	}
}
