package presentation.gui.editor;

import presentation.controllers.WorldController;
import presentation.main.Cord3S;
import presentation.objects.Orientation;
import presentation.objects.ViewData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Editor extends JLayeredPane implements IEditor {

    /**
     * The size of one tile, 16x16 + 1 pixel border
     */
    public static final byte SIZE = 17;

    private static final int BLOCKPANEL_INDEX = 10;

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
     * The width and height of the editor panel, depends on the orientation
     */
    private short width, height;

    /**
     * The unscaled buffer, is always ready
     */
    private BufferedImage unscaledBuffer;

    /**
     * The scaled buffer, can be out of date
     */
    private BufferedImage scaledBlockBuffer;

    /**
     * When true, the scaled buffer is out of date and should be updated
     */
    private boolean scaledBufferChanged;

    /**
     * The panel containing the block graphics
     */
    private BlockPanel blockPanel;

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

        unscaledBuffer = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);

        blockPanel = new BlockPanel(this);
        add(blockPanel, BLOCKPANEL_INDEX);

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
        super.paintComponent(graphics);

        if (scaledBufferChanged)
            generateScaledBuffer();

        Graphics2D g = (Graphics2D) graphics;
        g.drawImage(this.scaledBlockBuffer, 0, 0, null);
    }

    private void generateScaledBuffer() {

        if (scale != 1.0f)
            this.scaledBlockBuffer = scaleBufferedImage(unscaledBuffer, scale);
        else
            this.scaledBlockBuffer = this.unscaledBuffer;

        this.scaledBufferChanged = false;
    }

    private BufferedImage scaleBufferedImage(BufferedImage bi, float scale) {

        BufferedImage scaledImage = new BufferedImage((int) (bi.getWidth() * scale), (int) (bi.getHeight() * scale), bi.getType());

        AffineTransform at = new AffineTransform();
        at.scale(scale,scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return scaleOp.filter(bi, scaledImage);
    }

    @Override
    public BufferedImage getImage() {
        if (scaledBufferChanged)
            generateScaledBuffer();

        return scaledBlockBuffer;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;

        setScaledSizeChanged();
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setScaledSizeChanged() {
        scaledBufferChanged = true;
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

//    private void onSelectionUpdated(short curX, short curY) {
//
//        curX = (short) (curX / scale / SIZE);
//        curY = (short) (curY / scale / SIZE);
//
//        if ((curX == selectedX && curY == selectedY) ||
//                curX < 0 || curX >= width ||
//                curY < 0 || curY >= height)
//            return false;
//
//
//        selectedX = curX;
//        selectedY = curY;
//
//        selectCord(curX, curY);
//
//        Cord3S cord = getCords(selectedX, selectedY);
//        worldController.onSelectionUpdated(cord, this);
//
//        return true;
//    }
//
//    protected class MouseMoveHandler extends MouseMotionAdapter {
//
//        @Override
//        public void mouseMoved(MouseEvent e) {
//            onSelectionUpdated((short) e.getX(), (short) e.getY());
//        }
//
//        @Override
//        public void mouseDragged(MouseEvent e) {
//            onSelectionUpdated((short) e.getX(), (short) e.getY());
//        }
//    }
}
