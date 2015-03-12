package presentation.gui;

import javax.swing.*;

public class InternalWindow extends JInternalFrame {
	private static final long serialVersionUID = 7086467386852495806L;
	
	private static final int spawnDelta = 0;
	
	private final JComponent parent;
	
	protected InternalWindow(JComponent parent, String title, boolean visibleByDefault) {
		super(title, true, true, false, true);
		
		this.parent = parent;
		
		setLocation(spawnDelta, spawnDelta);
//		spawnDelta += 50;
		setVisible(visibleByDefault);

        parent.add(this);
		moveToFront();
	}

    @Override
    public void setBounds(int x, int y, int width, int height) {

    	int maxWidth = parent.getWidth();
    	int maxHeight = parent.getHeight();
    	
    	if (maxWidth == 0 || maxHeight == 0) {
    		super.setBounds(x, y, width, height);
    		return;
    	}
    	
    	if ((x + width) > maxWidth)
    		x -= (x + width) - maxWidth;
    	if ((y + height) > maxHeight)
    		y -= (y + height) - maxHeight;
    	
    	if (x < 0)
    		x = 0;
    	if (y < 0)
    		y = 0;
    	
    	if (width > maxWidth)
    		width = maxWidth;
    	if (height > maxHeight)
    		height = maxHeight;
    	
    	super.setBounds(x, y, width, height);
    }
}
