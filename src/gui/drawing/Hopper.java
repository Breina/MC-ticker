package gui.drawing;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Hopper {
	
	public static final byte TOP_LEFT	= 0;
	public static final byte TOP_RIGHT	= 1;
	public static final byte TOP_UP		= 2;
	public static final byte TOP_DOWN	= 3;
	public static final byte TOP_BACK	= 4;
	public static final byte SIDE_FRONT	= 5;
	public static final byte SIDE_BACK	= 6;
	public static final byte SIDE_RIGHT	= 7;
	public static final byte SIDE_LEFT	= 8;
	public static final byte SIDE_DOWN	= 9;
	
	private static void drawButton(Graphics2D g, byte side) {
		
		if (side >= SIDE_FRONT) {
			g.setColor(Colors.BACKGROUND);
			g.fill(new Rectangle2D.Float(0, 4, 16, 16));
			g.setColor(Colors.HOPPER_MAIN);
			g.fill(new Rectangle2D.Float(0, 0, 16, 4));
			g.fill(new Rectangle2D.Float(2, 4, 12, 4));
			g.fill(new Rectangle2D.Float(5, 8, 6, 4));
			
		} else {
			g.setColor(Colors.HOPPER_BORDER);
			g.fill(new Rectangle2D.Float(0, 0, 16, 16));
			g.setColor(Colors.HOPPER_MAIN);
			g.fill(new Rectangle2D.Float(2, 2, 12, 12));
			g.setColor(Colors.HOPPER_BORDER);
		}
		
		switch (side) {
			case TOP_LEFT:
				g.fill(new Rectangle2D.Float(2, 6, 6, 4));
				break;
			case TOP_RIGHT:
				g.fill(new Rectangle2D.Float(8, 6, 6, 4));
				break;
			case TOP_UP:
				g.fill(new Rectangle2D.Float(6, 2, 4, 6));
				break;
			case TOP_DOWN:
				g.fill(new Rectangle2D.Float(6, 8, 4, 6));
				break;
			case SIDE_BACK:
				g.setColor(Colors.HOPPER_BORDER);
				g.fill(new Rectangle2D.Float(6, 8, 4, 3));
				break;
			case SIDE_LEFT:
				g.fill(new Rectangle2D.Float(0, 9, 5, 3));
				break;
			case SIDE_RIGHT:
				g.fill(new Rectangle2D.Float(11, 9, 5, 3));
				break;
			case SIDE_DOWN:
				g.fill(new Rectangle2D.Float(6, 12, 4, 4));
		}
	}
	
	private static int getHashCode(byte side) {
		int hash = (side & 0b1111) << 6 | Keys.HOPPER;
		return hash;
	}
	
	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, byte side) {
		
		int hash = getHashCode(side);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawButton(g, side);
		
		buffer.put(hash, bi);		
		return bi;
	}
}
