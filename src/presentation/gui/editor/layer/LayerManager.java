package presentation.gui.editor.layer;

import logging.Log;
import presentation.controllers.WorldController;
import presentation.gui.editor.Editor;
import presentation.objects.Orientation;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Manages the layers of the Editor
 */
public class LayerManager {

    /**
     * For every editor, contains layers for all other editors
     */
    private final HashMap<Editor, HashMap<Editor, LayerPanel>> layers;

    private final WorldController worldController;

    public LayerManager(WorldController worldController) {

        this.worldController = worldController;

        layers = new HashMap<>(3);
    }

    /**
     * Adds a layer to all editors
     * @param newLayer The editor of the layer perspective that is added to our editor
     */
    public void addLayer(Editor newLayer) {

        HashMap<Editor, LayerPanel> editorLayers = new HashMap<>(layers.size());
        layers.put(newLayer, editorLayers);

        for (Editor editor : layers.keySet()) {

            if (editor.getOrientation() == newLayer.getOrientation())
                continue;

            // First add the new layer to every editor
            addLayer(editor, newLayer);

            // Then add every layer to the new one
            addLayer(newLayer, editor);
        }
    }

    private void addLayer(Editor editor, Editor newLayer) {

        LayerPanel layer = new LayerPanel(editor, newLayer);

        layers.get(editor).put(newLayer, layer);

        editor.setLayer(layer, Editor.LAYER_INDEX);
        editor.add(layer);

        updateLayerHeight(editor, newLayer, layer);
    }

    /**
     * Updates the layer to the correct height
     * @param layerEditor The editor of the layer perspective that is updated in our editor
     */
    public void updateLayer(Editor layerEditor) {

        Iterator<Editor> editorIterator = layers.keySet().iterator();
        Iterator<HashMap<Editor, LayerPanel>> layerPanelIterator = layers.values().iterator();

        while (editorIterator.hasNext()) {

            Editor editor = editorIterator.next();
            LayerPanel layerPanel = layerPanelIterator.next().get(layerEditor);

            if (editor == layerEditor ||
                    editor.getOrientation() == layerEditor.getOrientation())
                continue;

            if (layerPanel == null) {
                Log.w("Couldn't find layer " + layerEditor.getOrientation() + " in editor " + editor.getOrientation());
                continue;
            }

            updateLayerHeight(editor, layerEditor, layerPanel);
        }
    }

    private void updateLayerHeight(Editor editor, Editor addedLayer, LayerPanel panel) {
        short layerHeight = addedLayer.getLayerHeight();

        if (addedLayer.getOrientation() == Orientation.TOP)
            layerHeight = (short) (editor.getEditorHeight() - layerHeight - 1);

        panel.setLayerHeight(layerHeight);
    }

    /**
     * Removes a layer from all editors
     * @param layerEditor The editor of the layer perspective that is removed from all our editors
     */
    public void removeLayer(Editor layerEditor) {

        layers.remove(layerEditor);

        for (Editor editor : layers.keySet()) {

            LayerPanel layer = layers.get(editor).get(layerEditor);
        }
    }
}
