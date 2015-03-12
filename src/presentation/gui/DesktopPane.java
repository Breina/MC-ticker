package presentation.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DesktopPane extends JDesktopPane {
	private static final long serialVersionUID = -8373899151099524323L;
	
	private final Image backgroundImage;
	
	public DesktopPane() {
		super();
		
		setBackground(Color.DARK_GRAY);	
		
		// TODO check if can be removed
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent ce) {
				Dimension d = getSize();
				Component[] comps = getComponents();
                for (Component comp : comps) comp.setMaximumSize(d);
			}
		});
		
		ImageIcon icon = new ImageIcon("img/background.png");
		backgroundImage = icon.getImage();
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

//    private int DEBUGCOUNTER = 0;
//
//    @Override
//    public void paint(Graphics g) {
//
//        Log.d("painting");
//
//        DEBUGCOUNTER++;
//
////        if (DEBUGCOUNTER == 100) {
////            Log.printEntireStackTraceAndBeDoneWithIt();
////        }
//
//        super.paint(g);
//    }
}
