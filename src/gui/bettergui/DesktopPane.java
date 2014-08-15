package gui.bettergui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class DesktopPane extends JDesktopPane {
	private static final long serialVersionUID = -8373899151099524323L;
	
	private Image backgroundImage;
	
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
		
		ImageIcon icon = new ImageIcon("img/background.png");
		backgroundImage = icon.getImage();

//	    backgroundImage = image.getScaledInstance(1500, 1000, Image.SCALE_SMOOTH);
	}
	
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this);
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
