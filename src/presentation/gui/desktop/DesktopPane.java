package presentation.gui.desktop;

import javax.swing.*;
import java.awt.*;

public class DesktopPane extends JDesktopPane {
	private static final long serialVersionUID = -8373899151099524323L;
	
	private final Image backgroundImage;
	
	public DesktopPane() {
		super();
		
		setBackground(Color.DARK_GRAY);	
		
		// TODO check if can be removed
//		addComponentListener(new ComponentAdapter() {
//			@Override
//			public void componentResized(ComponentEvent ce) {
//				Dimension d = getSize();
//				Component[] comps = getComponents();
//                for (Component comp : comps) comp.setMaximumSize(d);
//			}
//		});
		
		ImageIcon icon = new ImageIcon("img/background.png");
		backgroundImage = icon.getImage();

        setDesktopManager(new BoundedDesktopManager());
	}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, this);
    }
}
