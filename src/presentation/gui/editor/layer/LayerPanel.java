package presentation.gui.editor.layer;

import logging.Log;
import presentation.controllers.MainController;
import presentation.gui.editor.Editor;
import presentation.gui.editor.EditorSubComponent;
import presentation.gui.windows.main.options.IPreferenceChangedListener;
import presentation.main.Constants;
import presentation.objects.Orientation;
import sim.constants.Prefs;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.prefs.Preferences;

class LayerPanel extends EditorSubComponent implements IPreferenceChangedListener {

    private MainController controller;
    private final boolean horizontal;
    private Color activeLayerColor;

    public LayerPanel(MainController controller, Editor editor, Editor layerEditor) {
        super(editor);

        controller.getOptionsController().registerPreferenceListener(Prefs.EDITOR_COLOR_LAYER, this);
        activeLayerColor = new Color(Preferences.userRoot().getInt(Prefs.EDITOR_COLOR_LAYER, Constants.COLORACTIVELAYER.getRGB()), true);

        Orientation layerOrientation = layerEditor.getOrientation();

        switch (orientation) {
            case TOP:
                switch (layerOrientation) {
                    case FRONT:
                        horizontal = true;
                        break;

                    case RIGHT:
                        horizontal = false;
                        break;

                    case TOP:
                    default:
                        throw new InternalError("Badly filtered layer addition");
                }
                break;

            case FRONT:
                switch (layerOrientation) {
                    case TOP:
                        horizontal = true;
                        break;

                    case RIGHT:
                        horizontal = false;
                        break;

                    case FRONT:
                    default:
                        throw new InternalError("Badly filtered layer addition");
                }
                break;

            case RIGHT:
                switch (layerOrientation) {
                    case TOP:
                        horizontal = true;
                        break;

                    case FRONT:
                        horizontal = false;
                        break;

                    case RIGHT:
                    default:
                        throw new InternalError("Badly filtered layer addition");
                }
                break;

            default:
                throw new InternalError("Badly filtered layer addition");
        }
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        Graphics2D g = (Graphics2D) gr;

        int length = (horizontal ? editorWidth : editorHeight);

        g.setColor(activeLayerColor);

        if (horizontal) {

            g.draw(new Rectangle2D.Float(0.5f, 0.5f, Editor.SIZE * length, Editor.SIZE));

            for (int i = 1; i < length; i++)
                g.draw(new Line2D.Float(Editor.SIZE * i + 0.5f, Editor.SIZE, Editor.SIZE * i + 0.5f, 0.5f));

        } else {

            g.draw(new Rectangle2D.Float(0.5f, 0.5f, Editor.SIZE, Editor.SIZE * length));

            for (int i = 1; i < length; i++)
                g.draw(new Line2D.Float(Editor.SIZE, Editor.SIZE * i + 0.5f, 0.5f, Editor.SIZE * i + 0.5f));
        }
    }

    public void setLayerHeight(short layerHeight) {

        if (horizontal)
            setBounds(0, Editor.SIZE * layerHeight, editorWidth * Editor.SIZE, (Editor.SIZE + 1));

        else
            setBounds(Editor.SIZE * layerHeight, 0, (Editor.SIZE + 1), editorHeight * Editor.SIZE);
    }

    @Override
    public void preferenceChanged(String preference) {
        Log.d(preference);
        activeLayerColor = new Color(Preferences.userRoot().getInt(Prefs.EDITOR_COLOR_LAYER, Constants.COLORACTIVELAYER.getRGB()), true);
    }
}
