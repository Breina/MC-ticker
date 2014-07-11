package gui.bettergui.editor;

import gui.drawing.Colors;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

public class EditorSelectionPanel extends JLabel {
	private static final long serialVersionUID = -1236645534312319619L;
	
	private int size;
	private float scale;
	
	private BufferedImage rectangle, scaledBuffer;

	public EditorSelectionPanel() {
		setOpaque(false);
		
		generateRectangle();
		
		setScale(1.0f);
		setVisible(false);
	}
	
	public void setScale(float scale) {
		if (this.scale == scale)
			return;
		
		size = (int) ((EditorPanel.SIZE + 1) * scale);
		
		this.scale = scale;
		setPreferredSize(new Dimension(size, size));
		scaledBuffer = EditorPanel.scaleBufferedImage(rectangle, scale);
	}
	
	private void generateRectangle() {
		rectangle = new BufferedImage(EditorPanel.SIZE + 1, EditorPanel.SIZE + 1, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D) rectangle.getGraphics();

		g.setColor(Colors.SELECTION);
		
		g.drawLine(0, 0, 0, EditorPanel.SIZE);
		g.drawLine(0, 0, EditorPanel.SIZE, 0);
		g.drawLine(EditorPanel.SIZE, EditorPanel.SIZE, 0, EditorPanel.SIZE);
		g.drawLine(EditorPanel.SIZE, EditorPanel.SIZE, EditorPanel.SIZE, 0);
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		
		g.drawImage(scaledBuffer, null, 0, 0);
	}

}
