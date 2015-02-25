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

    private SelectionManager selectionManager;

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

        start = getCord2D(start3D.x, start3D.y, start3D.z);
        end = getCord2D(end3D.x, end3D.y, end3D.z);

        Graphics2D g = (Graphics2D) gr;

        // Border
        g.setColor(Constants.COLORSELECTIONBORDER);
        g.setStroke(new BasicStroke(2));

        g.draw(new Rectangle2D.Float(start.x * Editor.SIZE, start.y * Editor.SIZE,
                (end.x - start.x + 1) * Editor.SIZE + 1f, (end.y - start.y + 1) * Editor.SIZE + 1f));

        // Interior
        g.setColor(Constants.COLORSELECTIONINTERIOR);
        for (short x = 0; x < width; x++)
            for (short y = 0; y < height;y++) {
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
}
