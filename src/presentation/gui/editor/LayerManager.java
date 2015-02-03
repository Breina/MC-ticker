package presentation.gui.editor;

import presentation.objects.Orientation;

import java.util.HashMap;

/**
 * Manages the layers of the Editor
 */
public class LayerManager {

    /**
     * Holds all the layer objects
     */
    private HashMap<Editor, LayerPanel> layers;

    private Editor editor;

    public LayerManager(Editor editor) {

        this.editor = editor;

        layers = new HashMap<>(3);
    }

    /**
     * Adds a layer to the editor
     * @param layerEditor The editor of the layer perspective that is added to our editor
     */
    public void addLayer(Editor layerEditor) {

        LayerPanel layer = new LayerPanel(editor, layerEditor);

        layers.put(layerEditor, layer);

        editor.setLayer(layer, Editor.LAYER_INDEX);
        editor.add(layer);

        updateLayer(layerEditor);
    }

    /**
     * Updates the layer to the correct height
     * @param layerEditor The editor of the layer perspective that is updated in our editor
     */
    public void updateLayer(Editor layerEditor) {

        LayerPanel layer = layers.get(layerEditor);
        short layerHeight = layerEditor.getLayerHeight();

        if (layerEditor.getOrientation() == Orientation.TOP)
            layerHeight = (short) (editor.getEditorHeight() - layerHeight - 1);

        layer.setLayerHeight(layerHeight);
    }

    /**
     * Removes a layer from the editor
     * @param layerEditor The editor of the layer perspective that is removed from our editor
     */
    public void removeLayer(Editor layerEditor) {

        LayerPanel layer = layers.remove(layerEditor);

        editor.remove(layer);
    }
}
