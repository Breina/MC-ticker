package gui.drawing;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;


public class Slab {
	
	public static final boolean UP		= false;
	public static final boolean DOWN	= true;
	
	public static final boolean TOP		= false;
	public static final boolean SIDE	= true;
	
	private static void drawSlab(Graphics2D g, boolean type, boolean side) {
		
		if (side == TOP) {
			g.setColor(Colors.SLAB);
			g.fill(new Rectangle2D.Float(0, 0, 16, 16));
			return;
		}
		
		if (type == UP)
			g.setColor(Colors.SLAB);
		else
			g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(0, 0, 16, 8));
		
		if (type == DOWN)
			g.setColor(Colors.SLAB);
		else
			g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(0, 8, 16, 8));
	}

	private static int getHashCode(boolean type, boolean side) {
		int hash = (type ? 1 : 0) << 7 | (side ? 1 : 0) << 6 | Keys.SLAB;
		return hash;
	}

	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, boolean type, boolean side) {
		
		int hash = getHashCode(type, side);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawSlab(g, type, side);
		
		buffer.put(hash, bi);		
		return bi;			
	}
}
