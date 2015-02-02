package presentation.gui.editor;

import java.awt.image.BufferedImage;

/**
 * The methods that the editor needs to implement.
 */
public interface IEditor {

    /**
     * Repaint everything except the layers
     */
    public void updateAll();

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
     * Sets the current height of this layer, should not exceed the max nor be below 0
     * @param layer The layer
     */
    public void setLayerHeight(short layer);

    /**
     * Adds a layer to this editor
     * @param editor The other perspective of this schematic
     */
    public void addLayer(Editor editor);

    /**
     * Gets information out of the other editor and updates the layer in this Editor
     * @param editor The other perspective of this schematic
     */
    public void updateLayer(Editor editor);

    /**
     * Removes a layer from this editor
     * @param editor The other perspective of this schematic
     */
    public void remoreLayer(Editor editor);
}
