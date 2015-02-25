package presentation.gui.editor.block;

import presentation.main.Constants;
import presentation.objects.Orientation;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Redstone wire has its own drawing class because we don't want 16 * (5 + 3) = 128 images of 174 bytes (avg.) = 22 KB of images, but instead use a 3.5 KB class.
 * That and it's a lot faster.
 */
public class RedstoneWire {
	
	private static HashMap<Integer, BufferedImage> bufferedImages = new HashMap<>();
	
	private static final int powerStep	= 9;
	private static final int minPower	= 120;
	private static final int offPower	= 64;
	
	/**
	 * Draws a redstone wire
	 * @param g The graphics to be drawn upon
	 * @param powerLevel Its brightness
	 * @param orientation TOP, FRONT or RIGHT
	 * @param cons Up, left, down, right
	 */
	private static void drawWire(Graphics2D g, byte powerLevel, Orientation orientation, boolean[] cons) {
		
		g.setColor(Constants.COLORBACKGROUND);
		g.fill(new Rectangle2D.Float(0, 0, 16, 16));
		
		// Apologies for the oneliner, didn't want to create extra vars 
		g.setColor(new Color((powerLevel == 0) ? offPower : minPower + powerStep * powerLevel, 0, 0));

		// If there's a bug in this code, good luck.
		switch (orientation) {
			case TOP:
				if ((cons[0] || cons[2]) && !cons[1] && !cons[3]) {
					g.fill(new Rectangle2D.Float(6, 0, 4, 16));
					break;
				}
				if ((cons[1] || cons[3]) && !cons[0] && !cons[2]) {
					g.fill(new Rectangle2D.Float(0, 6, 16, 4));
					break;
				}
				if (cons[0])
					g.fill(new Rectangle2D.Float(6, 0, 4, 6));
				if (cons[1])
					g.fill(new Rectangle2D.Float(0, 6, 6, 4));
				if (cons[2])
					g.fill(new Rectangle2D.Float(6, 10, 4, 6));
				if (cons[3])
					g.fill(new Rectangle2D.Float(10, 6, 6, 4));
				g.fill(new Rectangle2D.Float(6, 6, 4, 4));
				break;
				
			case FRONT:
				if ((cons[1] || cons[3]) && !cons[0] && !cons[2]) {
					g.fill(new Rectangle2D.Float(0, 12, 16, 4));
					break;
				}
				if (cons[1])
					g.fill(new Rectangle2D.Float(0, 12, 6, 4));
				if (cons[3])
					g.fill(new Rectangle2D.Float(10, 12, 6, 4));
				g.fill(new Rectangle2D.Float(6, 12, 4, 4));
				break;
				
			case RIGHT:
				if ((cons[0] || cons[2]) && !cons[1] && !cons[3]) {
					g.fill(new Rectangle2D.Float(0, 12, 16, 4));
					break;
				}
				if (cons[0])
					g.fill(new Rectangle2D.Float(10, 12, 6, 4));
				if (cons[2])
					g.fill(new Rectangle2D.Float(0, 12, 6, 4));
				g.fill(new Rectangle2D.Float(6, 12, 4, 4));
				break;
		}
	}
	
	/**
	 * Returns a hashcode for the hashMap
	 * @param powerLevel 4 bits
     * @param orientation the orientation
	 * @param cons 4 bits
	 * @return 16 bits
	 */
	private static int getHashCode(byte powerLevel, Orientation orientation, boolean[] cons) {
		byte con = (byte) ((cons[0] ? 1 : 0) << 3 | (cons[1] ? 1 : 0) << 2 | (cons[2] ? 1 : 0) << 1 | (cons[3] ? 1 : 0));
		
		byte ori = 0;

		switch (orientation) {
			case TOP:
				ori = 0;
				break;
			case RIGHT:
				ori = 1;
				break;
			case FRONT:
				ori = 2;
		}
		
		int hash = (ori & 0b11) << 8 | (powerLevel & 0b1111) << 4 | con;
		return hash;
	}

	public static BufferedImage draw(byte powerLevel, Orientation orientation, boolean[] connections) {
		
		int hash = getHashCode(powerLevel, orientation, connections);
		if (bufferedImages.containsKey(hash))
			return bufferedImages.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawWire(g, powerLevel, orientation, connections);
		
		bufferedImages.put(hash, bi);
		return bi;
	}
}
