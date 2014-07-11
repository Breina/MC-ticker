package gui.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;


public class RedstoneRepeater {
	
	public static final byte TOP_LEFT	= 0;
	public static final byte TOP_RIGHT	= 1;
	public static final byte TOP_UP		= 2;
	public static final byte TOP_DOWN	= 3;
	public static final byte SIDE_LEFT	= 4;
	public static final byte SIDE_RIGHT	= 5;
	public static final byte SIDE_BACK	= 6;
	public static final byte SIDE_FRONT	= 7;
	
	
	
	private static void drawRepeater(Graphics2D g, boolean on, byte side, byte delay) {
		
		g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(0, 0, 16, 16));
		
		Color powerColor;
		
		if (on)
			powerColor = Colors.REDSTONE_ON;
		else
			powerColor = Colors.REDSTONE_OFF;
		
		g.setColor(Colors.REPEATER_BACK);
		
		switch (side) {
			case TOP_LEFT:
				for (byte b = 0; b < 4; b++) {
					if (b == delay) g.setColor(powerColor);
					g.fill(new Rectangle2D.Float(12 - 4 * b, 2 * b, 4, 16 - 4 * b));
				}
				break;
			case TOP_RIGHT:
				for (byte b = 0; b < 4; b++) {
					if (b == delay) g.setColor(powerColor);
					g.fill(new Rectangle2D.Float(4 * b, 2 * b, 4, 16 - 4 * b));
				}
				break;
			case TOP_UP:
			case SIDE_BACK:
				for (byte b = 0; b < 4; b++) {
					if (b == delay) g.setColor(powerColor);
					g.fill(new Rectangle2D.Float(2 * b, 12 - 4 * b, 16 - 4 * b, 4));
				}
				break;
			case TOP_DOWN:
			case SIDE_FRONT:
				for (byte b = 0; b < 4; b++) {
					if (b == delay) g.setColor(powerColor);
					g.fill(new Rectangle2D.Float(2 * b, 4 * b, 16 - 4 * b, 4));
				}
				break;
			case SIDE_LEFT:
				for (byte b = 0; b < 4; b++) {
					if (b == delay) g.setColor(powerColor);
					g.fill(new Rectangle2D.Float(12 - 4 * b, 6 + 2 * b, 4, 10 - 2 * b));
				}
				break;
			case SIDE_RIGHT:
				for (byte b = 0; b < 4; b++) {
					if (b == delay) g.setColor(powerColor);
					g.fill(new Rectangle2D.Float(4 * b, 6 + 2 * b, 4, 10 - 2 * b));
				}
		}
	}
	
	/**
	 * Returns the hashcode used by the hashMap
	 * @param on 1 bit
	 * @param side 3 bits
	 * @param delay 2 bits
	 * @return 16 bits
	 */
	private static int getHashCode(boolean on, byte side, byte delay) {
		int hash = (side & 0b111) << 9 | (on ? 1 : 0) << 8 | (delay & 0b11) << 6 | Keys.REDSTONE_REPEATER;
		return hash;
	}
	
	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, boolean on, byte side, byte delay) {
		
		int hash = getHashCode(on, side, delay);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawRepeater(g, on, side, delay);
		
		buffer.put(hash, bi);
		return bi;
	}
}
