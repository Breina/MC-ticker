package presentation.gui.editor;

import presentation.controllers.WorldController;
import presentation.main.Cord3S;
import presentation.objects.Orientation;

import javax.swing.*;

/**
 * A JPanel that provides some utilities for subcomponents
 */
public class EditorSubComponent extends JPanel {

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

    public EditorSubComponent(Editor editor) {
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
    protected Cord3S getCords(short x, short y) {

        Cord3S cords = null;
        short layer = editor.getLayerHeight();

        switch (orientation) {
            case TOP:
                cords = new Cord3S(x, layer, y);
                break;

            case FRONT:
                cords = new Cord3S(x, (short) (height - y - 1), layer);
                break;

            case RIGHT:
                cords = new Cord3S(layer, (short) (height - y - 1), (short) (width - x - 1));
                break;
        }

        return cords;
    }
}
