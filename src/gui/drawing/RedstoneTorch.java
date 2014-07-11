package gui.drawing;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;


public class RedstoneTorch {
	
	public static final byte LEFT	= 0;
	public static final byte RIGHT	= 1;
	public static final byte UP		= 2;
	public static final byte DOWN	= 3;
	public static final byte FRONT	= 4;
	public static final byte BACK	= 5;
	
	
	/**
	 * Draws a redstone torch
	 * @param g The graphics to draw it on 
	 * @param on If it's turned on or off
	 * @param side LEFT, RIGHT, UP, DOWN, FRONT or BACK
	 */
	private static void drawTorch(Graphics2D g, boolean on, byte side) {
		
		g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(0, 0, 16, 16));
		
		g.setColor(Colors.TORCH_HANDLE);
		switch (side) {
			case LEFT:
				g.fill(new Rectangle2D.Float(0, 6, 5, 4));
				break;
			case RIGHT:
				g.fill(new Rectangle2D.Float(11, 6, 5, 4));
				break;
			case UP:
				g.fill(new Rectangle2D.Float(6, 0, 4, 5));
				break;
			case DOWN:
				g.fill(new Rectangle2D.Float(6, 11, 4, 5));
		}
		
		g.setColor((on) ? Colors.REDSTONE_ON : Colors.REDSTONE_OFF);
		
		g.fill(new Ellipse2D.Float(4, 4, 8, 8));
		
		if (side == BACK) {
			g.setColor(Colors.TORCH_HANDLE);
			g.fill(new Ellipse2D.Float(6, 6, 4, 4));			
		}
	}
	
	/**
	 * Returns a hashcode for the hashMap
	 * @param on 1 bit
	 * @param side 3 bits
	 * @return 16 bit hash
	 */
	private static int getHashCode(boolean on, byte side) {
		int hash = (side & 0b111) << 7 | (on ? 1 : 0) << 6 | Keys.REDSTONE_TORCH;
		return hash;
	}

	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, boolean on, byte side) {
		
		int hash = getHashCode(on, side);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawTorch(g, on, side);
		
		buffer.put(hash, bi);		
		return bi;			
	}
}
