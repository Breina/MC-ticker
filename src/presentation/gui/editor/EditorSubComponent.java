package presentation.gui.editor;

import presentation.controllers.WorldController;
import presentation.main.Cord2S;
import presentation.main.Cord3S;
import presentation.objects.Orientation;

import javax.swing.*;
import java.awt.*;

/**
 * A JPanel that provides some utilities for subcomponents
 */
public abstract class EditorSubComponent extends JPanel {

    protected final WorldController worldController;

    /**
     * The orientation of this panel
     */
    protected final Orientation orientation;

    /**
     * The width and height of the editor panel, depends on the orientation
     */
    protected final short width, height;

    /**
     * A reference to the Editor that includes this panel
     */
    protected final Editor editor;

    protected EditorSubComponent(Editor editor) {
        super();

        this.editor = editor;
        this.worldController = editor.getWorldController();
        this.width = editor.getEditorWidth();
        this.height = editor.getEditorHeight();
        this.orientation = editor.getOrientation();

        setOpaque(false);

        int pixelWidth = width * Editor.SIZE;
        int pixelHeight = height * Editor.SIZE;

        setBounds(0, 0, pixelWidth, pixelHeight);
    }

    /**
     * Gets the 3D cord depending on the 2D cord, the orientation and the layer
     * @param x The 2D x coord, which is between 0 inclusive and width exclusive
     * @param y The 2D y coord, which is between 0 inclusive and height exclusive
     * @return The 3D cord
     */
    protected Cord3S getCord3D(short x, short y) {

        short layer = editor.getLayerHeight();

        switch (orientation) {
            case TOP:
                return new Cord3S(x, layer, y);

            case FRONT:
                return new Cord3S(x, (short) (height - y - 1), layer);

            case RIGHT:
                return new Cord3S(layer, (short) (height - y - 1), (short) (width - x - 1));
        }

        return null;
    }

    /**
     * Translates 3D cords to 2D tile cords depending on the layer and the orientation.
     * @param x
     * @param y
     * @param z
     * @return The 2D cord if the layer is in the range, otherwise null.
     */
    Cord2S getCord2D(short x, short y, short z) {

        short layer = editor.getLayerHeight();

        switch (orientation) {
            case TOP:
                if (y != layer)
                    return null;

                return new Cord2S(x, z);

            case FRONT:
                if (z != layer)
                    return null;

                return new Cord2S(x, (short) (height - y - 1));

            case RIGHT:
                if (x != layer)
                    return null;

                return new Cord2S((short) (width - z - 1), (short) (height - y - 1));
        }

        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {

        editor.repaint();

        super.paintComponents(g);
    }
}
