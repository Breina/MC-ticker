package gui.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;


public class Comparator {
	
	public static final boolean MODE_COMPARE	= false;
	public static final boolean MODE_SUBTRACT	= true;
	
	public static final byte TOP_LEFT	= 0;
	public static final byte TOP_RIGHT	= 1;
	public static final byte TOP_UP		= 2;
	public static final byte TOP_DOWN	= 3;
	public static final byte SIDE_LEFT	= 4;
	public static final byte SIDE_RIGHT	= 5;
	public static final byte SIDE_BACK	= 6;
	public static final byte SIDE_FRONT	= 7;
	
	private static void drawComparator(Graphics2D g, boolean on, boolean mode, byte side) {
		
		g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(0, 0, 16, 16));
		
		Color powerColor;
		
		if (on)
			powerColor = Colors.REDSTONE_ON;
		else
			powerColor = Colors.REDSTONE_OFF;
		
		Color modeColor;
		
		if (mode == MODE_COMPARE)
			modeColor = Colors.REDSTONE_OFF;
		else
			modeColor = Colors.REDSTONE_ON;
		
		g.setColor(Colors.REPEATER_BACK);
				
		switch (side) {
			case TOP_UP:
			case SIDE_BACK:
				// JAVA I FUCKING HATE YOU!
				int[] x0 = {0, 16, 8};
				int[] y0 = {16, 16, 0};
				g.fill(new Polygon(x0, y0, 3));
				g.setColor(powerColor);
				g.fill(new Rectangle2D.Float(4, 11, 8, 3));
				g.setColor(modeColor);
				g.fill(new Rectangle2D.Float(7, 2, 2, 2));
				break;
			case TOP_LEFT:
				int[] x1 = {0, 16, 16};
				int[] y1 = {8, 16, 0};
				g.fill(new Polygon(x1, y1, 3));
				g.setColor(powerColor);
				g.fill(new Rectangle2D.Float(11, 4, 3, 8));
				g.setColor(modeColor);
				g.fill(new Rectangle2D.Float(2, 7, 2, 2));
				break;
			case TOP_DOWN:
			case SIDE_FRONT:
				int[] x2= {0, 16, 8};
				int[] y2 = {0, 0, 16};
				g.fill(new Polygon(x2, y2, 3));
				g.setColor(powerColor);
				g.fill(new Rectangle2D.Float(4, 2, 8, 3));
				g.setColor(modeColor);
				g.fill(new Rectangle2D.Float(7, 12, 2, 2));
				break;
			case TOP_RIGHT:
				int[] x3 = {16, 0, 0};
				int[] y3 = {8, 16, 0};
				g.fill(new Polygon(x3, y3, 3));
				g.setColor(powerColor);
				g.fill(new Rectangle2D.Float(2, 4, 3, 8));
				g.setColor(modeColor);
				g.fill(new Rectangle2D.Float(12, 7, 2, 2));
				break;
			case SIDE_LEFT:
				int[] x4 = {0, 0, 16, 16};
				int[] y4 = {12, 16, 16, 0};
				g.fill(new Polygon(x4, y4, 4));
				g.setColor(powerColor);
				g.fill(new Rectangle2D.Float(11, 6, 3, 8));
				g.setColor(modeColor);
				g.fill(new Rectangle2D.Float(2, 12, 2, 2));
				break;
			case SIDE_RIGHT:
				int[] x5 = {16, 16, 0, 0};
				int[] y5 = {12, 16, 16, 0};
				g.fill(new Polygon(x5, y5, 4));
				g.setColor(powerColor);
				g.fill(new Rectangle2D.Float(2, 6, 3, 8));
				g.setColor(modeColor);
				g.fill(new Rectangle2D.Float(12, 12, 2, 2));
		}
	}
	
	/**
	 * Returns the hashcode for the hashmap
	 * @param on 1 bit
	 * @param mode 1 bit
	 * @param side 3 bits
	 * @return 16 bits
	 */
	private static int getHashCode(boolean on, boolean mode, byte side) {
		int hash = (side & 0b111) << 8 | (on ? 1 : 0) << 7 | (mode ? 1 : 0) << 6 | Keys.COMPARATOR;
		return hash;
	}
	
	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, boolean on, boolean mode, byte side) {
		
		int hash = getHashCode(on, mode, side);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawComparator(g, on, mode, side);
		
		buffer.put(hash, bi);
		return bi;
	}
}
