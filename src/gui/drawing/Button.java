package gui.drawing;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Button {
	
	public static final byte TOP_LEFT	= 0;
	public static final byte TOP_RIGHT	= 1;
	public static final byte TOP_UP		= 2;
	public static final byte TOP_DOWN	= 3;
	public static final byte SIDE_FRONT	= 4;
	public static final byte SIDE_BACK	= 5;
	public static final byte SIDE_RIGHT	= 6;
	public static final byte SIDE_LEFT	= 7;
	
	private static void drawButton(Graphics2D g, byte side, boolean pressed, boolean wooden) {
		
		g.setColor(Colors.BACKGROUND);
		g.fill(new Rectangle2D.Float(0, 0, 16, 16));
		
		g.setColor(wooden ? Colors.WOOD : Colors.STONE);
		
		byte mod = (byte) (pressed ? 1 : 0);
		
		switch (side) {
			case TOP_LEFT:
				g.fill(new Rectangle2D.Float(0, 5, 2 - mod, 6));
				break;
			case TOP_RIGHT:
				g.fill(new Rectangle2D.Float(14 + mod, 5, 2 - mod, 6));
				break;
			case TOP_UP:
				g.fill(new Rectangle2D.Float(5, 0, 6, 2 - mod));
				break;
			case TOP_DOWN:
				g.fill(new Rectangle2D.Float(5, 14 + mod, 6, 2 - mod));
				break;
			case SIDE_FRONT:
			case SIDE_BACK:
				g.fill(new Rectangle2D.Float(5, 6, 6, 4));
				break;
			case SIDE_LEFT:
				g.fill(new Rectangle2D.Float(0, 6, 2 - mod, 4));
				break;
			case SIDE_RIGHT:
				g.fill(new Rectangle2D.Float(14 + mod, 6, 2 - mod, 4));				
		}
	}
	
	private static int getHashCode(byte side, boolean pressed, boolean wooden) {
		int hash = (side & 0b111) << 8 | (pressed ? 1 : 0) << 7 | (wooden ? 1 : 0) << 6 | Keys.BUTTON;
		return hash;
	}
	
	public static BufferedImage draw(HashMap<Integer, BufferedImage> buffer, byte side, boolean pressed, boolean wooden) {
		
		int hash = getHashCode(side, pressed, wooden);
		if (buffer.containsKey(hash))
			return buffer.get(hash);
		
		BufferedImage bi;
		bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		
		drawButton(g, side, pressed, wooden);
		
		buffer.put(hash, bi);		
		return bi;
	}
}
