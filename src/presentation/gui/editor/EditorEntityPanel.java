package presentation.gui.editor;

import presentation.main.Constants;
import presentation.objects.Entity;
import presentation.objects.Orientation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EditorEntityPanel extends JLabel {
	private static final long serialVersionUID = -1236645534312319619L;

	private float scale;
	private Entity entity;

	private Orientation orientation;

	private BufferedImage rectangle, scaledBuffer;

	public EditorEntityPanel(Entity entity, Orientation orientation) {

		this.entity = entity;
		this.orientation = orientation;

		setOpaque(false);
		generate();
		
		setScale(1.0f);
	}
	
	public void setScale(float scale) {
		if (this.scale == scale)
			return;
		
		this.scale = scale;
		setPreferredSize(new Dimension(	(int) (entity.getWidth() * scale),
										(int) (entity.getHeight() * scale)));
		scaledBuffer = EditorPanel.scaleBufferedImage(rectangle, scale);
	}
	
	private void generate() {
		rectangle = new BufferedImage(EditorPanel.SIZE + 1, EditorPanel.SIZE + 1, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D) rectangle.getGraphics();

		float width = entity.getWidth() * EditorPanel.SIZE;
		float height;

		if (orientation == Orientation.TOP)
			height = width;
		else
			height = entity.getHeight() * EditorPanel.SIZE;

		g.setColor(Constants.COLORENTITY);
		g.drawRect(0, 0, (int) width, (int) height);

		if (entity.isDead()) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, (int) width, (int) height);
			g.drawLine(0, (int) height, (int) width, 0);
		}

		// Vector
		g.setColor(Constants.COLORENTITYVECTOR);

		float x = 0.5f;
		float y = 0.5f;

		switch (orientation) {
			case TOP:
				if (entity.getvX() != 0)
					x += entity.getvX() * Constants.ENTITYVELOCITYMULTIPLIER;

				if (entity.getvZ() != 0)
					y += entity.getvZ() * Constants.ENTITYVELOCITYMULTIPLIER;

				break;

			case FRONT:
				if (entity.getvX() != 0)
					x += entity.getvX() * Constants.ENTITYVELOCITYMULTIPLIER;

				if (entity.getvY() != 0)
					y += entity.getvY() * Constants.ENTITYVELOCITYMULTIPLIER;

				break;

			case RIGHT:
				if (entity.getvZ() != 0)
					x -= entity.getvZ() * Constants.ENTITYVELOCITYMULTIPLIER;

				if (entity.getvY() != 0)
					y += entity.getvY() * Constants.ENTITYVELOCITYMULTIPLIER;
		}

		if (x != 0.5f | y != 0.5f)
			g.drawLine(	(int) (width / 2), (int) (height / 2),
						(int) (x * width), (int) (y * height));
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		
		g.drawImage(scaledBuffer, null, 0, 0);
	}
}
