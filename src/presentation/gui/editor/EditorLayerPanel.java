package presentation.gui.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import presentation.main.Constants;

public class EditorLayerPanel extends JLabel {
	private static final long serialVersionUID = -3084881419783032686L;
	private int size;
	private final boolean horizontal;
	private final short length;
	private float scale;
	
	private BufferedImage rectangle, scaledBuffer;

	public EditorLayerPanel(boolean horizontal, short length) {
		setOpaque(false);
		
		this.horizontal = horizontal;
		this.length = length;
		
		generateRectangle();
		
		setScale(1.0f);
	}
	
	public void setScale(float scale) {
		
		if (this.scale == scale)
			return;
		
		size = (int) ((EditorPanel.SIZE + 1) * scale);
		
		this.scale = scale;
		setPreferredSize(new Dimension(size * (horizontal ? length : 1), size * (!horizontal ? length : 1)));
		scaledBuffer = EditorPanel.scaleBufferedImage(rectangle, scale);
	}
	
	private void generateRectangle() {
		
		int size = EditorPanel.SIZE + 1;
		rectangle = new BufferedImage(size * (horizontal ? length : 1), size * (!horizontal ? length : 1), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D) rectangle.getGraphics();

		g.setColor(Constants.COLORACTIVELAYER);
		
		if (horizontal) {
			
			g.drawLine(0, 0, 0, EditorPanel.SIZE);
			g.drawLine(0, 0, EditorPanel.SIZE * length, 0);
			g.drawLine(EditorPanel.SIZE * length, EditorPanel.SIZE, 0, EditorPanel.SIZE);
			
			for (int i = 1; i < length; i++)
				g.drawLine(EditorPanel.SIZE * i, EditorPanel.SIZE, EditorPanel.SIZE * i, 0);
			
		} else {
			
			g.drawLine(0, 0, EditorPanel.SIZE, 0);
			g.drawLine(0, 0, 0, EditorPanel.SIZE * length);
			g.drawLine(EditorPanel.SIZE, EditorPanel.SIZE * length, EditorPanel.SIZE, 0);
			
			for (int i = 1; i < length; i++)
				g.drawLine(EditorPanel.SIZE, EditorPanel.SIZE * i, 0, EditorPanel.SIZE * i);
		}
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		
		g.drawImage(scaledBuffer, null, 0, 0);
	}
	
	public boolean isHorizontal() {
		return horizontal;
	}

}
