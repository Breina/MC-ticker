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

    public SelectionPanel(Editor editor) {
        super(editor);

        clearSelection();
    }

    /**
     * Clears the entire selection
     */
    public void clearSelection() {
        selectionMask = new boolean[width][height];
    }

    /**
     * Selects or unselects an entire region
     * @param start The start of the selection, will determine if the region gets selected or unselected
     * @param end The end of the selection
     */
    public void selectRegion(Cord2S start, Cord2S end) {

        boolean select = !selectionMask[start.x][start.y];

        setSelectedRectangle(start, end);

        for (int x = this.start.x; x < this.end.x; x++)
            for (int y = this.start.y; y < this.end.y; y++)
                selectionMask[x][y] = select;
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
     */
    public void selectTile(Cord2S cord) {

        this.start = cord;
        this.end = cord;

        selectionMask[cord.x][cord.y] = !selectionMask[cord.x][cord.y];
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        Graphics2D g = (Graphics2D) gr;

        g.setColor(Constants.COLORSELECTIONBORDER);
        g.setStroke(new BasicStroke(2));
        g.draw(new Rectangle2D.Float(start.x * Editor.SIZE, start.y * Editor.SIZE,
                end.x * Editor.SIZE + 1f, end.y * Editor.SIZE + 1f));
    }
}
