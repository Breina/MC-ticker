package presentation.gui.editor;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.InternalError;
import logging.Log;
import presentation.controllers.TileController;
import presentation.controllers.WorldController;
import presentation.exceptions.UnhandledBlockDataException;
import presentation.exceptions.UnhandledBlockIdException;
import presentation.main.Cord2S;
import presentation.main.Cord3S;
import presentation.objects.Block;
import presentation.objects.Entity;
import presentation.objects.Orientation;
import presentation.objects.ViewData;
import presentation.tools.Tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class EditorPanel extends JLayeredPane {
	private static final long serialVersionUID = -7955350531422459430L;
	
	private TileController tileController;
	private WorldController worldController;
	
	private short layer;
	private float scale;
	private final Orientation orientation;
	
	private short width, height;
	
	private JPanel blockPanel;
	private BufferedImage drawBlockBuffer, scaledBlockBuffer;
	private boolean scaledBufferChanged;

	private ConcurrentHashMap<EditorPanel, EditorLayerPanel> layers;
	private ConcurrentHashMap<Entity, EditorEntityPanel> entities;

	// TODO properties
	public static final boolean LAYER_HIGHLIGHTING = true;
	public static final boolean SELECTION_HIGHLIGHTING = true;

	public static final byte SIZE = 17; // 16x16 + 1 pixel border
	
	private int dragButton;

	public EditorPanel(WorldController controller, Orientation orientation) {
		this(controller, (short) 0, 2.0f, orientation);
	}

	public EditorPanel(WorldController worldController, short layer, float scale, Orientation orientation) {
		super();

		this.worldController = worldController;
		this.tileController = worldController.getMainController().getTileController();
		this.layer = layer;
		this.orientation = orientation;

        extractWorldDimensions();

        int pixelWidth = width * SIZE;
		int pixelHeight = height * SIZE;
		
		drawBlockBuffer = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);

		layers = new ConcurrentHashMap<>();
		entities = new ConcurrentHashMap<>();
		
		blockPanel = new JPanel() {
			private static final long serialVersionUID = 4506583861488260246L;

			@Override
			protected void paintComponent(Graphics gr) {
				if (scaledBufferChanged)
					generateScaledBuffer();
				
				Graphics2D g = (Graphics2D) gr;
				g.drawImage(scaledBlockBuffer, null, 0, 0);
			}
		};
		
		setScale(scale);
		
//		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
//		addMouseMotionListener(new MouseMoveHandler());
//		addMouseListener(new MouseHandler());
		
		Tool tool = worldController.getMainController().getTool();
		addMouseListener(tool);
		if (tool.hasMouseMotionListener())
			addMouseMotionListener((MouseMotionListener) tool);
	}

	/**
	 * Grabs the correct sizes for the width and height dimensions, depending on the orientation
	 */
	private void extractWorldDimensions() {
		
		ViewData viewData = worldController.getWorldData();
		
		switch (orientation) {
			
			case TOP:
				width = viewData.getXSize();
				height = viewData.getZSize();
				break;
				
			case FRONT:
				width = viewData.getXSize();
				height = viewData.getYSize();
				break;
				
			case RIGHT:
				width = viewData.getZSize();
				height = viewData.getYSize();	
		}
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		if (scaledBufferChanged)
			generateScaledBuffer();
		
		Graphics2D g = (Graphics2D) graphics;
		g.drawImage(this.scaledBlockBuffer, 0, 0, null);
	}
	
	private void generateScaledBuffer() {
		
		if (scale != 1.0f)
			this.scaledBlockBuffer = scaleBufferedImage(drawBlockBuffer, scale);
		else
			this.scaledBlockBuffer = this.drawBlockBuffer;
		
		this.scaledBufferChanged = false;
	}
	
	public BufferedImage getImage() {
		
		if (scaledBufferChanged)
			generateScaledBuffer();
		
		return scaledBlockBuffer;
	}

	// Fix this so it's no longer static
	public static BufferedImage scaleBufferedImage(BufferedImage bi, float scale) {
		
		BufferedImage scaledImage = new BufferedImage((int) (bi.getWidth() * scale), (int) (bi.getHeight() * scale), bi.getType());
		
		AffineTransform at = new AffineTransform();
		at.scale(scale,scale);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		
		return scaleOp.filter(bi, scaledImage);
	}
	
	public void repaintAll() {
		
		repaintBlocks();
		
		repaint();
	}
	
	private void repaintBlocks() {
		
		scaledBufferChanged = true;
		
		Graphics2D g = (Graphics2D) drawBlockBuffer.getGraphics();
		g.setBackground(Color.GRAY);
		g.clearRect(0, 0, width * SIZE + 1, height * SIZE + 1);
		
		for (short y = 0; y < this.height; y++)
			for (short x = 0; x < this.width; x++) {
				Cord3S cords = getCords(x, y);
				BufferedImage image;
				
				try {
					image = getTile(cords.x, cords.y, cords.z);
					g.drawImage(image, x * SIZE + 1, y * SIZE + 1, null);
					
				} catch (UnhandledBlockDataException | UnhandledBlockIdException e) {
					
					Log.e(e.getMessage());
				}
			}
	}

	public void setLayer(short layer) {
		if (this.layer == layer)
			return;
		
		this.layer = layer;
		
		repaintBlocks();
		checkEntityVisibility();
		repaint();
	}
	
	public short getLayer() {
		return this.layer;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		
		Dimension scaledSize = getScaledSize();
		
		setPreferredSize(scaledSize);
		
		blockPanel.setPreferredSize(scaledSize);
		blockPanel.setBounds(new Rectangle(scaledSize));

		if (LAYER_HIGHLIGHTING) {
			Iterator<EditorPanel> eps = layers.keySet().iterator();
			Iterator<EditorLayerPanel> ls = layers.values().iterator();
			
			while (eps.hasNext()) {
				EditorPanel ep = eps.next();
				EditorLayerPanel l = ls.next();
				
				l.setScale(scale);
				updateLayer(ep);
			}
		}

		Iterator<Entity> es = entities.keySet().iterator();
		Iterator<EditorEntityPanel> eeps = entities.values().iterator();

		while (eeps.hasNext()) {
			EditorEntityPanel eep = eeps.next();

			eep.setScale(scale);
			configurateEntity(es.next(), eep);
		}
		
		this.repaintAll();
	}
	
	public float getScale() {
		return scale;
	}
	
	private Dimension getScaledSize() {
		return new Dimension((int) ((width * SIZE) * scale),
							(int) ((height * SIZE) * scale));
	}

	private BufferedImage getTile(short x, short y, short z) throws UnhandledBlockDataException, UnhandledBlockIdException {
		
		BufferedImage tileImage;
		Block b = worldController.getWorldData().getBlock(x, y, z);
		
		if (b == null)
			return null;
		
		switch (b.getId()) {
			case Block.BLOCK_WIRE:
				tileImage = getRedstoneWire(b, x, y, z);
				return tileImage;
				
			case Block.BLOCK_CHEST:
				tileImage = getChest(b, x, y, z);
				return tileImage;
		}
		
		tileImage = tileController.getTile(b.getId(), b.getData(), orientation);
		
		return tileImage;
	}
	
	private BufferedImage getRedstoneWire(Block b, short x, short y, short z) {
		byte powerLevel = b.getData();
		
		ViewData viewData = worldController.getWorldData();
		BufferedImage bi = null;
		boolean[] cons = new boolean[4];
		
		final short[] XCORDS = {x, (short) (x - 1), x, (short) (x + 1)};
		final short[] ZCORDS = {(short) (z - 1), z, (short) (z + 1), z};
		
		
		final boolean up = !viewData.getBlock(x, (short) (y + 1), z).isSolidBlock();
		
		Block testBlock;
		for (byte i = 0; i < 4; i++) {
			testBlock = viewData.getBlock(XCORDS[i], y, ZCORDS[i]);
					// anything on same level
			cons[i] = (testBlock.isConnectable(i % 2 == 0) ? true : false)
					// wire 1 block lower
					|| (!testBlock.isSolidBlock() && viewData.getBlock(XCORDS[i], (short) (y - 1), ZCORDS[i]).getId() == Block.BLOCK_WIRE)
					// wire 1 block higher
					|| (up && viewData.getBlock(XCORDS[i], (short) (y + 1), ZCORDS[i]).getId() == Block.BLOCK_WIRE);			
		}
  		
  		bi = RedstoneWire.draw(powerLevel, this.orientation, cons);  		
		
		return bi;
	}

	// TODO
	private BufferedImage getChest(Block b, short x, short y, short z) {
//		tileController.getTile(id, data, orientation)
		return null;
	}
	
	private Cord3S getCords(short x, short y) {

		Cord3S cords = null;

		switch (orientation) {
			case TOP:
				cords = new Cord3S(x, this.layer, y);
				break;
				
			case FRONT:
				cords = new Cord3S(x, (short) (height - y - 1), this.layer);
				break;
				
			case RIGHT:
				cords = new Cord3S(this.layer, (short) (height - y - 1), (short) (width - x - 1));
				break;
		}

		return cords;
	}
	
	private Cord2S getCords(short x, short y, short z) {
		
		switch (orientation) {
			case TOP:
				if (y != layer)
					return null;
				
				return new Cord2S(x,  z);
				
			case FRONT:
				if (z != layer)
					return null;
				
				return new Cord2S(x, (short) (height - y - 1));
				
			case RIGHT:
				if (x != layer)
					return null;
				
				return new Cord2S((short) (width - z - 1), (short) (height - y - 1));
				
			default:
			case UNDEFINED:
				return null;
		}
	}

	public BufferedImage generateImage() {
		generateScaledBuffer();
		return this.scaledBlockBuffer;
	}
	
//	private boolean onSelectionUpdated(short curX, short curY) {
//
//		if (!SELECTION_HIGHLIGHTING)
//			return false;
//
//		curX = (short) (curX / scale / SIZE);
//		curY = (short) (curY / scale / SIZE);
//
//		if ((curX == selectedX && curY == selectedY) ||
//				curX < 0 || curX >= width ||
//				curY < 0 || curY >= height)
//			return false;
//
//
//		selectedX = curX;
//		selectedY = curY;
//
//		selectCord(curX, curY);
//
//		Cord3S cord = getCords(selectedX, selectedY);
//		worldController.onSelectionUpdated(cord, this);
//
//		return true;
//	}
//
//	protected class MouseMoveHandler extends MouseMotionAdapter {
//
//		@Override
//		public void mouseMoved(MouseEvent e) {
//			onSelectionUpdated((short) e.getX(), (short) e.getY());
//		}
//
//		@Override
//		public void mouseDragged(MouseEvent e) {
//			onSelectionUpdated((short) e.getX(), (short) e.getY());
//		}
//	}
	
	public void addLayer(EditorPanel ep) {
		
		Orientation orientation = ep.getOrientation();
		EditorLayerPanel layerPanel;
		
		switch (this.orientation) {
			case TOP:
				switch (orientation) {
					case FRONT:
						layerPanel = new EditorLayerPanel(true, width);
						break;
						
					case RIGHT:
						layerPanel = new EditorLayerPanel(false, height);
						break;
						
					default:
					case TOP:
					case UNDEFINED:
						throw new InternalError("Badly filtered layer addition");
				}
				break;
				
			case FRONT:
				switch (orientation) {
					case TOP:
						layerPanel = new EditorLayerPanel(true, width);
						break;
						
					case RIGHT:
						layerPanel = new EditorLayerPanel(false, height);
						break;
						
					default:
					case FRONT:
					case UNDEFINED:
						throw new InternalError("Badly filtered layer addition");
				}
				break;
				
			case RIGHT:
				switch (orientation) {
					case TOP:
						layerPanel = new EditorLayerPanel(true, width);
						break;
						
					case FRONT:
						layerPanel = new EditorLayerPanel(false, height);
						break;
						
					default:
					case RIGHT:
					case UNDEFINED:
						throw new InternalError("Badly filtered layer addition");
				}
				break;
				
			default:
			case UNDEFINED:
				throw new InternalError("Corrupted orientation");
		}
		
		layerPanel.setScale(scale);
		layers.put(ep, layerPanel);
		add(layerPanel, 40);
		
		updateLayer(ep);
	} 
	
	public void updateLayer(EditorPanel ep) {
		
		EditorLayerPanel layer = layers.get(ep);
		short layerIndex = ep.getLayer();
		
		if (ep.getOrientation() == Orientation.TOP)
			layerIndex = (short) (height - layerIndex - 1);
		
		if (layer.isHorizontal())
			layer.setBounds(0, (int) (SIZE * scale * layerIndex), (int) (width * SIZE * scale), (int) ((SIZE + 1) * scale));
		
		else
			layer.setBounds((int) (SIZE * scale * layerIndex), 0, (int) ((SIZE + 1) * scale), (int) (height * SIZE * scale));
	}

	public void removeLayer(EditorPanel ep) {

		EditorLayerPanel layer = layers.get(ep);

		if (layer != null) {
			remove(layer);
			layers.remove(ep);
		}
	}

	private void addEntity(Entity entity) {

		EditorEntityPanel pnl = new EditorEntityPanel(entity, orientation);
		pnl.setScale(scale);
		entities.put(entity, pnl);

		configurateEntity(entity, pnl);

		add(pnl, 30);
	}

	private void configurateEntity(Entity entity, EditorEntityPanel pnl) {
		float scaledWidth = entity.getWidth() * SIZE * scale;
		float scaledHeight = entity.getHeight() * SIZE * scale;
		double scaledX = entity.getX() * SIZE * scale;
		double scaledY = entity.getY() * SIZE * scale;
		double scaledZ = entity.getZ() * SIZE * scale;

		switch (orientation) {
			case TOP:
				pnl.setBounds(	(int) (scaledX - scaledWidth / 2),
						(int) (scaledZ - scaledWidth / 2),
						(int) (scaledWidth + scale), (int) (scaledWidth + scale));
				break;

			case FRONT:
				pnl.setBounds(	(int) (scaledX - scaledWidth / 2),
						(int) (height * scale * SIZE - scaledY - scaledHeight),
						(int) (scaledWidth + scale), (int) (scaledHeight + scale));
				break;

			case RIGHT:
				pnl.setBounds(	(int) (width * SIZE * scale - scaledZ - scaledWidth / 2),
						(int) (height * scale * SIZE - scaledY - scaledHeight),
						(int) (scaledWidth + scale), (int) (scaledHeight + scale));
		}

		checkEntityVisibility(entity, pnl);
	}

	private void checkEntityVisibility(Entity entity, EditorEntityPanel pnl) {

		switch (orientation) {
			case TOP:
				pnl.setVisible(
						entity.getY() + entity.getHeight()  >= layer &&
						entity.getY() <= layer + 1
				);
				break;

			case FRONT:
				pnl.setVisible(
						entity.getZ() + entity.getWidth() / 2 >= layer &&
						entity.getZ() - entity.getWidth() / 2 <= layer + 1
				);
				break;

			case RIGHT:
				pnl.setVisible(
						entity.getX() + entity.getWidth() / 2 >= layer &&
								entity.getX() + entity.getWidth() / 2 <= layer + 1
				);
		}
	}

	private void checkEntityVisibility() {

		Iterator<Entity> entityIterator = entities.keySet().iterator();
		Iterator<EditorEntityPanel> panelIterator = entities.values().iterator();

		while (entityIterator.hasNext())
			checkEntityVisibility(entityIterator.next(), panelIterator.next());
	}

	public synchronized void updateEntities(Entity[] ents) {

		// Remove previous entities (unless you find a way to hook up the onEntityRemoved)
		Iterator<EditorEntityPanel> panelIterator = entities.values().iterator();
		while (panelIterator.hasNext()) {
			remove(panelIterator.next());
			panelIterator.remove();
		}

		// Add them again
		for (Entity entity : ents)
			addEntity(entity);
	}
}