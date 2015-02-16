package presentation.gui.editor;

import presentation.controllers.WorldController;
import presentation.main.Cord3S;
import presentation.objects.Orientation;
import presentation.objects.ViewData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

/**
 * Container for logic regarding the editor. Handles:
 *  - Scaling
 *  - Layers
 *  - Selection
 */
public class Editor extends JLayeredPane {

    /**
     * The size of one tile, 16x16 + 1 pixel border
     */
    public static final byte SIZE = 17;

    private static final int BLOCK_INDEX = 10;
    public static final int LAYER_INDEX = 20;
    public static final int ENTITY_INDEX = 40;
    private static final int SELECTION_INDEX = 50;

    private final WorldController worldController;

    /**
     * The orientation of this panel
     */
    private final Orientation orientation;

    /**
     * Which height of layer we're on
     */
    private short layer;

    /**
     * The scale of the editor
     */
    private float scale;

    /**
     * The width and height of the editor panel in tiles, depends on the orientation
     */
    private short width, height;

    /**
     * The width and height of the editor panel in pixels
     */
    private int pixelWidth, pixelHeight;

    /**
     * The panel containing the block graphics
     */
    private BlockPanel blockPanel;

    /**
     * The panel containing the mouse cursor block
     */
    private SelectionPanel selectionPanel;

    /**
     * The layer manager to handle all the layers xD
     */
    private LayerManager layerManager;

    /**
     * The manager of all the entities
     */
    private EntityManager entityManager;

    public Editor(WorldController worldController, Orientation orientation) {
        this(worldController, (short) 0, 2.0f, orientation);
    }

    public Editor(WorldController worldController, short layer, float scale, Orientation orientation) {
        super();

        this.worldController = worldController;
        this.orientation = orientation;

        layerManager = new LayerManager(this);
        entityManager = new EntityManager(this);

        setLayerHeight(layer);
        setScale(scale);

        extractWorldDimensions();

        pixelWidth = (int) (width * SIZE * scale);
        pixelHeight = (int) (height * SIZE * scale);

        blockPanel = new BlockPanel(this);
        setLayer(blockPanel, BLOCK_INDEX);
        add(blockPanel);

        selectionPanel = new SelectionPanel(this);
        setLayer(selectionPanel, SELECTION_INDEX);
        add(selectionPanel);

        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        addMouseMotionListener(new MouseMoveHandler());
        addMouseListener(new MouseHandler());

        onSchematicUpdated();

        setPreferredSize(new Dimension(pixelWidth, pixelHeight));
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
        Graphics2D g = (Graphics2D) graphics;
        g.scale(scale, scale);
    }

    /**
     * Renders the current image without selection nor layers, used for export
     * @return A BufferedImage containing said image
     */
    public BufferedImage getImage() {

        BufferedImage img = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        paintComponent(img.createGraphics());

        return img;
    }

    /**
     * Sets the scale of the image, a scale of 1.0f will show the images as they are
     * @param scale The scale
     */
    public void setScale(float scale) {
        this.scale = scale;

        setPreferredSize(new Dimension((int) ((width * SIZE) * scale),
                (int) ((height * SIZE) * scale)));
    }

    /**
     * Gets the scale of the image, a scale of 1.0f will show the images as they are
     * @return The scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the current height of this layer, should not exceed the max nor be below 0.
     * Must be called after entityManager is initialized
     * @param layer The layer height
     */
    public void setLayerHeight(short layer) {
        this.layer = layer;

        entityManager.checkVisibility();
    }

    /**
     * Gets the current height of this layer, will not exceed the max nor be below 0
     * @return The layer height
     */
    public short getLayerHeight() {
        return layer;
    }

    /**
     * Selects the 3D dimensional cord in the editor
     * @param cord The cord
     */
    public void selectCord(Cord3S cord) {
        selectionPanel.selectCord(cord);
    }

    /**
     * Returns the tile width of the editor
     * @return The width
     */
    public short getEditorWidth() {
        return width;
    }

    /**
     * Returns the tile height of the editor
     * @return The height
     */
    public short getEditorHeight() {
        return height;
    }

    /**
     * Gets the orientation of the editor
     * @return The orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

    public WorldController getWorldController() {
        return worldController;
    }

    public LayerManager getLayerManager() {
        return layerManager;
    }

    public void onSchematicUpdated() {
        entityManager.updateEntities();

        repaint();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Handles updating the selection of the mouse cursor
     */
    protected class MouseMoveHandler extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            selectionPanel.onSelectionUpdated((short) e.getX(), (short) e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            selectionPanel.onSelectionUpdated((short) e.getX(), (short) e.getY());
        }
    }

    /**
     * Handles making the selection (in)visible when moving in and out of the editor
     */
    protected class MouseHandler extends MouseAdapter {
        @Override
        public void mouseExited(MouseEvent e) {
            selectionPanel.selectCord(null);

            worldController.onSelectionUpdated(null, Editor.this);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            selectionPanel.setVisible(true);
        }
    }
}