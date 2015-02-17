package presentation.gui.editor;

import presentation.main.Constants;
import presentation.main.Cord2S;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The excel-style selection panel
 */
public class SelectionPanel extends EditorSubComponent {

    /**
     * A mask indicating which columns are selected
     */
    private boolean[][] selectionMask;

    /**
     * Remembering the outline
     */
    private Cord2S start;
    private Cord2S end;

    /**
     * Whether we should draw or not
     */
    private boolean draw;

    public SelectionPanel(Editor editor) {
        super(editor);

        clearSelection();
    }

    /**
     * Clears the entire selection
     */
    public void clearSelection() {
        selectionMask = new boolean[width][height];

        draw = false;
    }

    /**
     * Selects or unselects an entire region
     * @param start The start of the selection, will determine if the region gets selected or unselected
     * @param end The end of the selection
     * @param select To select or not
     * @param calculateMask Whether or not to update the mask thingy
     */
    public void selectRegion(Cord2S start, Cord2S end, boolean select, boolean calculateMask) {

        setSelectedRectangle(start, end);

        if (calculateMask)
            for (int x = this.start.x; x <= this.end.x; x++)
                for (int y = this.start.y; y <= this.end.y; y++)
                    selectionMask[x][y] = select;

        draw = true;
    }

    private void setSelectedRectangle(Cord2S start, Cord2S end) {

        // Makes it so that start is top left and end is bottom right
        if (start.x < end.x) {

            if (start.y < end.y) {

                this.start = start;
                this.end = end;

            } else {

                this.start = new Cord2S(start.x, end.y);
                this.end = new Cord2S(end.x, start.y);
            }
        } else {

            if (start.y < end.y) {

                this.start = new Cord2S(end.x, start.y);
                this.end = new Cord2S(start.x, end.y);

            } else {

                this.start = end;
                this.end = start;
            }
        }
    }

    /**
     * Selects a single tile
     * @param cord The tile to select
     * @param select To select or not
     */
    public void selectTile(Cord2S cord, boolean select) {

        this.start = cord;
        this.end = cord;

        selectionMask[cord.x][cord.y] = select;

        draw = true;
    }

    /**
     * Returns whether the tile is selected or not
     * @param cord The cord
     * @return True if it's been selected
     */
    public boolean tileSelected(Cord2S cord) {
        return selectionMask[cord.x][cord.y];
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        if (!draw)
            return;

        Graphics2D g = (Graphics2D) gr;

        // Border
        g.setColor(Constants.COLORSELECTIONBORDER);
        g.setStroke(new BasicStroke(2));

        g.draw(new Rectangle2D.Float(start.x * Editor.SIZE, start.y * Editor.SIZE,
                (end.x - start.x + 1) * Editor.SIZE + 1f, (end.y - start.y + 1) * Editor.SIZE + 1f));

        // Interior
        g.setColor(Constants.COLORSELECTIONINTERIOR);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height;y++)
                if (selectionMask[x][y])
                    g.fill(new Rectangle2D.Float(x * Editor.SIZE + 1f, y * Editor.SIZE + 1f,
                            Editor.SIZE, Editor.SIZE));
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
