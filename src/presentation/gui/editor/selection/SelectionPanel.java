package presentation.gui.editor.selection;

import presentation.controllers.MainController;
import presentation.gui.editor.Editor;
import presentation.gui.editor.EditorSubComponent;
import presentation.gui.windows.main.options.IPreferenceChangedListener;
import presentation.main.Constants;
import presentation.main.Cord2S;
import presentation.main.Cord3S;
import sim.constants.Prefs;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.prefs.Preferences;

/**
 * The excel-style selection panel
 */
public class SelectionPanel extends EditorSubComponent implements IPreferenceChangedListener {

    /**
     * Remembering the outline
     */
    private Cord2S start;
    private Cord2S end;

    private Color selectionBorderColor, selectionInteriorColor;

    private final SelectionManager selectionManager;

    public SelectionPanel(MainController controller, Editor editor) {
        super(editor);

        controller.getOptionsController().registerPreferenceListener(Prefs.EDITOR_COLOR_SELECTIONBORDER, this);
        selectionBorderColor = new Color(Preferences.userRoot().getInt(Prefs.EDITOR_COLOR_SELECTIONBORDER, Constants.COLORSELECTIONBORDER.getRGB()), true);

        controller.getOptionsController().registerPreferenceListener(Prefs.EDITOR_COLOR_SELECTIONINTERIOR, this);
        selectionInteriorColor = new Color(Preferences.userRoot().getInt(Prefs.EDITOR_COLOR_SELECTIONINTERIOR, Constants.COLORSELECTIONINTERIOR.getRGB()), true);

        selectionManager = editor.getWorldController().getSelectionManager();
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        if (!selectionManager.isAnythingSelected())
            return;

        Cord3S start3D = selectionManager.getStart();
        Cord3S end3D = selectionManager.getEnd();

        projectBoundedRectangle(start3D, end3D);

        Graphics2D g = (Graphics2D) gr;

        // Border
        if (start != null && end != null) {
            g.setColor(selectionBorderColor);
            g.setStroke(new BasicStroke(2));

            g.draw(new Rectangle2D.Float(start.x * Editor.SIZE, start.y * Editor.SIZE,
                    (end.x - start.x + 1) * Editor.SIZE + 1f, (end.y - start.y + 1) * Editor.SIZE + 1f));
        }

        // Interior
        g.setColor(selectionInteriorColor);
        for (short x = 0; x < editorWidth; x++)
            for (short y = 0; y < editorHeight;y++) {
                Cord3S c = getCord3D(x, y);
                if (selectionManager.isSelected(c.x, c.y, c.z))
                    g.fill(new Rectangle2D.Float(x * Editor.SIZE + 1f, y * Editor.SIZE + 1f,
                            Editor.SIZE, Editor.SIZE));
            }
    }

    /**
     * Checks if the mouse position is on the border
     * @param p The mouse location
     * @return True if the mouse is above the border
     */
    public boolean isPositionOnBorder(Point p) {

        if (start == null || end == null)
            return false;

        Rectangle2D outer = new Rectangle2D.Float(start.x * Editor.SIZE - 1f, start.y * Editor.SIZE - 1f,
                (end.x - start.x + 1) * Editor.SIZE + 3f, (end.y - start.y + 1) * Editor.SIZE + 3f);
        Rectangle2D inner = new Rectangle2D.Float(start.x * Editor.SIZE + 1f, start.y * Editor.SIZE + 1f,
                (end.x - start.x + 1) * Editor.SIZE - 1f, (end.y - start.y + 1) * Editor.SIZE - 1f);


        double mouseX = p.getX() / editor.getScale();
        double mouseY = p.getY() / editor.getScale();

        return outer.contains(mouseX, mouseY) && !inner.contains(mouseX, mouseY);
    }

    private void projectBoundedRectangle(Cord3S start, Cord3S end) {

        short layer = editor.getLayerHeight();

        switch (orientation) {
            case TOP:
                if (layer < start.y || layer > end.y) {
                    this.start = null;
                    this.end = null;
                    return;
                }

                this.start = new Cord2S(start.x, start.z);
                this.end = new Cord2S(end.x, end.z);
                break;

            case FRONT:
                if (layer < start.z || layer > end.z) {
                    this.start = null;
                    this.end = null;
                    return;
                }

                this.start = new Cord2S(start.x, (short) (editorHeight - end.y - 1));
                this.end = new Cord2S(end.x, (short) (editorHeight - start.y - 1));
                break;

            case RIGHT:
                if (layer < start.x || layer > end.x) {
                    this.start = null;
                    this.end = null;
                    return;
                }

                this.end = new Cord2S((short) (editorWidth - start.z - 1), (short) (editorHeight - start.y - 1));
                this.start = new Cord2S((short) (editorWidth - end.z - 1), (short) (editorHeight - end.y - 1));
                break;

            default:
                throw new IllegalStateException("Badly defined layer :(");
        }

    }

    @Override
    public void preferenceChanged(String preference) {
        switch (preference) {
            case Prefs.EDITOR_COLOR_SELECTIONBORDER:
                selectionBorderColor = new Color(Preferences.userRoot().getInt(Prefs.EDITOR_COLOR_SELECTIONBORDER, Constants.COLORSELECTIONBORDER.getRGB()), true);
                break;

            case Prefs.EDITOR_COLOR_SELECTIONINTERIOR:
                selectionInteriorColor = new Color(Preferences.userRoot().getInt(Prefs.EDITOR_COLOR_SELECTIONINTERIOR, Constants.COLORSELECTIONINTERIOR.getRGB()), true);
                break;
        }
    }
}
