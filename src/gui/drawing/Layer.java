package gui.drawing;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Layer {
	
	public static final boolean HORIZONTAL	= true;
	public static final boolean VERTICAL	= false;
	
	private static void drawLayer(Graphics2D g, boolean side, short length) {
		
		g.setColor(Colors.ACTIVELAYER);
		
		final short max = (short) (17 * length);
		
		if (side == HORIZONTAL) {
			g.drawLine(0, 0, max, 0);
			g.drawLine(0, 17, max, 17);
			
			for (short x = 0; x <= max; x += 17)
				g.drawLine(x, 0, x, 17);
		} else {
			g.drawLine(0, 0, 0, max);
			g.drawLine(17, 0, 17, max);
			
			for (short y = 0; y <= max; y += 17)
				g.drawLine(0, y, 17, y);
		}
	}
	
	private static int getHashCode(boolean side, short length) {
		int hash = length << 7 | (side ? 1 : 0) << 6 | Keys.LAYER;
		return hash;
	}
	
	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, boolean side, short length) {
		
		int hash = getHashCode(side, length);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = (side == HORIZONTAL ? new BufferedImage(length * 17, 17 + 1, BufferedImage.TYPE_INT_ARGB_PRE) : new BufferedImage(17 + 1, length * 17, BufferedImage.TYPE_INT_ARGB_PRE));
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawLayer(g, side, length);
		
		buffer.put(hash, bi);		
		return bi;
	}
}
