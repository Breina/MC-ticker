package gui.drawing;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Glass {

	public static final boolean TOP		= false;
	public static final boolean SIDE	= true;
	
	private static void drawGlass(Graphics2D g) {
		
		g.setColor(Colors.GLASS_AND_ICE);
		g.fill(new Rectangle2D.Float(0, 0, 16, 16));
		
		g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(2, 2, 12, 12));
	}

	private static int getHashCode() {
		int hash = Keys.GLASS;
		return hash;
	}
	
	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer) {
		
		int hash = getHashCode();
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawGlass(g);
		
		buffer.put(hash, bi);
		return bi;
	}
}
