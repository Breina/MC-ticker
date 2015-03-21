package presentation.gui.editor.selection;

import presentation.gui.editor.Editor;
import presentation.gui.editor.EditorSubComponent;
import presentation.main.Constants;
import presentation.main.Cord2S;
import presentation.main.Cord3S;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The excel-style selection panel
 */
public class SelectionPanel extends EditorSubComponent {

    /**
     * Remembering the outline
     */
    private Cord2S start;
    private Cord2S end;

    private final SelectionManager selectionManager;

    public SelectionPanel(Editor editor) {
        super(editor);

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
            g.setColor(Constants.COLORSELECTIONBORDER);
            g.setStroke(new BasicStroke(2));

            g.draw(new Rectangle2D.Float(start.x * Editor.SIZE, start.y * Editor.SIZE,
                    (end.x - start.x + 1) * Editor.SIZE + 1f, (end.y - start.y + 1) * Editor.SIZE + 1f));
        }

        // Interior
        g.setColor(Constants.COLORSELECTIONINTERIOR);
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

            case UNDEFINED:
            default:
                throw new IllegalStateException("Badly defined layer :(");
        }

    }
}
