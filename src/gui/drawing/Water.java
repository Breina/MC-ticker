package gui.drawing;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Water {
	
	public static final byte MAX_HEIGHT = 0; // inverted
	
	private static void drawButton(Graphics2D g, byte data) {
		
		g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(0, 0, 16, 16));
		
		g.setColor(Colors.WATER);
		data *= 2;
		g.fill(new Rectangle2D.Float(0, data, 16, (16 - data)));
	}
	
	private static int getHashCode(byte data) {
		int hash = (data & 0b111) << 6 | Keys.WATER;
		return hash;
	}
	
	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, byte data) {
		
		int hash = getHashCode(data);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawButton(g, data);
		
		buffer.put(hash, bi);		
		return bi;
	}
}
