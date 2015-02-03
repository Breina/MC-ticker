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

public class Editor extends JLayeredPane implements IEditor {

    /**
     * The size of one tile, 16x16 + 1 pixel border
     */
    public static final byte SIZE = 17;

    private static final int BLOCK_INDEX = 10;
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
     * When true, the scaled buffer is out of date and should be updated
     */
    private boolean scaledBufferChanged;

    /**
     * The panel containing the block graphics
     */
    private BlockPanel blockPanel;

    /**
     * The panel containing the mouse cursor block
     */
    private SelectionPanel selectionPanel;

    public Editor(WorldController worldController, Orientation orientation) {
        this(worldController, (short) 0, 2.0f, orientation);
    }

    public Editor(WorldController worldController, short layer, float scale, Orientation orientation) {
        super();

        this.worldController = worldController;
        this.orientation = orientation;

        setLayerHeight(layer);
        setScale(scale);

        extractWorldDimensions();

        int pixelWidth = width * SIZE;
        int pixelHeight = height * SIZE;

        blockPanel = new BlockPanel(this);
        add(blockPanel, BLOCK_INDEX);

        selectionPanel = new SelectionPanel(this);
        add(selectionPanel, SELECTION_INDEX);

        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        addMouseMotionListener(new MouseMoveHandler());
        addMouseListener(new MouseHandler());

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

        super.paintComponent(g);
    }

    @Override
    public BufferedImage getImage() {

        BufferedImage img = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        paintComponent(img.createGraphics());

        return img;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;

        setPreferredSize(new Dimension((int) ((width * SIZE) * scale),
                (int) ((height * SIZE) * scale)));
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setLayerHeight(short layer) {
        this.layer = layer;
    }

    @Override
    public short getLayerHeight() {
        return layer;
    }

    @Override
    public void addLayer(IEditor editor) {

    }

    @Override
    public void updateLayer(IEditor editor) {

    }

    @Override
    public void removeLayer(IEditor editor) {

    }

    @Override
    public void selectCord(Cord3S cord) {

    }

    @Override
    public void unselect() {

    }

    public short getEditorWidth() {
        return width;
    }

    public short getEditorHeight() {
        return height;
    }

    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    public WorldController getWorldController() {
        return worldController;
    }

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

    protected class MouseHandler extends MouseAdapter {
        @Override
        public void mouseExited(MouseEvent e) {
            selectionPanel.onSelectionUpdated((short) -1, (short) -1);

        // TODO
//            unSelectOthers();

            worldController.onSelectionUpdated(null, null);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            selectionPanel.setVisible(true);
        }
    }
}
