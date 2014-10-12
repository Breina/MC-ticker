package presentation;

import java.util.List;

import javax.swing.JInternalFrame;

import presentation.controllers.MainController;

public class InternalWindow extends JInternalFrame {
	private static final long serialVersionUID = 7086467386852495806L;
	
	private static int spawnDelta = 0; 
	
	private MainController controller;
	
	public InternalWindow(MainController controller, String title) {
		super(title, true, true, false, true);
		
		this.controller = controller;
		
		setLocation(spawnDelta, spawnDelta);
//		spawnDelta += 50;
		
		setVisible(true);
		controller.getDesktop().add(this);
		moveToFront();
	}

    @Override
    public void setBounds(int x, int y, int width, int height) {
    	
    	DesktopPane desktop = controller.getDesktop();
    	int maxWidth = desktop.getWidth();
    	int maxHeight = desktop.getHeight();
    	
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
