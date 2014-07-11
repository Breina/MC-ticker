package gui.bettergui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class DesktopPane extends JDesktopPane {
	private static final long serialVersionUID = -8373899151099524323L;
	
	public DesktopPane() {
		super();
		
		setBackground(Color.DARK_GRAY);		
		
		// TODO check if can be removed
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent ce) {
				Dimension d = getSize();
				Component[] comps = getComponents();
				for (int i = 0; i < comps.length; i++)
					comps[i].setMaximumSize(d);
			}
		});
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
				
		for (Component c : getComponents()) {
			// TODO: Make components expand again if there is room again
			c.setBounds(c.getBounds());
		}
	}
}
