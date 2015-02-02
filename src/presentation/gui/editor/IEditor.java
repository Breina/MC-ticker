package presentation.gui.editor;

import presentation.main.Cord3S;
import presentation.objects.Orientation;

import java.awt.image.BufferedImage;

/**
 * The methods that the editor needs to implement.
 */
public interface IEditor {

    /**
     * Gets the orientation of the editor
     * @return The orientation
     */
    public Orientation getOrientation();

    /**
     * Renders the current image without selection nor layers, used for export
     * @return A BufferedImage containing said image
     */
    public BufferedImage getImage();

    /**
     * Sets the scale of the image, a scale of 1.0f will show the images as they are
     * @param scale The scale
     */
    public void setScale(float scale);

    /**
     * Gets the scale of the image, a scale of 1.0f will show the images as they are
     * @return The scale
     */
    public float getScale();

    /**
     * Informs the implementation that the scaled buffer is out of date,
     * recommended action is to render it again before a repaint
     */
    public void setScaledSizeChanged();

    /**
     * Sets the current height of this layer, should not exceed the max nor be below 0
     * @param layer The layer height
     */
    public void setLayerHeight(short layer);

    /**
     * Gets the current height of this layer, will not exceed the max nor be below 0
     * @return The layer height
     */
    public short getLayerHeight();

    /**
     * Adds a layer to this editor
     * @param editor The other perspective of this schematic
     */
    public void addLayer(IEditor editor);

    /**
     * Gets information out of the other editor and updates the layer in this Editor
     * @param editor The other perspective of this schematic
     */
    public void updateLayer(IEditor editor);

    /**
     * Removes a layer from this editor
     * @param editor The other perspective of this schematic
     */
    public void removeLayer(IEditor editor);

    /**
     * Selects the 3D dimensional cord in the editor
     * @param cord The cord
     */
    public void selectCord(Cord3S cord);

    /**
     * Makes the selection invisible again
     */
    public void unselect();
}
