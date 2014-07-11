package gui.drawing;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class PressurePlate {
	
	public static final boolean TOP		= false;
	public static final boolean SIDE	= true;
	
	public static final byte WOOD	= 0;
	public static final byte STONE	= 1;
	public static final byte IRON	= 2;
	public static final byte GOLD	= 3;
	
	private static void drawPlate(Graphics2D g, boolean side, boolean pressed, byte type) {
		
		g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(0, 0, 16, 16));
		
		switch (type) {
			case WOOD:
				g.setColor(Colors.WOOD);
				break;
			case STONE:
				g.setColor(Colors.STONE);
				break;
			case IRON:
				g.setColor(Colors.IRON);
				break;
			case GOLD:
				g.setColor(Colors.GOLD);
		}
		
		
		if (side == TOP)
			g.fill(new Rectangle2D.Float(1, 1, 14, 14));
		else
			if (pressed)
				g.fill(new Rectangle2D.Float(1f, 15.5f, 14f, 0.5f));
			else
				g.fill(new Rectangle2D.Float(1, 15, 14, 1));
	}

	private static int getHashCode(boolean side, boolean pressed, byte type) {
		int hash = (side ? 1 : 0) << 9 | (pressed ? 1 : 0) << 8 | (type & 0b11) << 6 | Keys.PRESSURE_PLATE;
		return hash;
	}
	
	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, boolean side, boolean pressed, byte type) {
		
		int hash = getHashCode(side, pressed, type);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawPlate(g, side, pressed, type);
		
		buffer.put(hash, bi);
		return bi;
	}
}
