package presentation.gui.editor;

import presentation.main.Constants;
import presentation.main.Cord3S;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SelectionPanel extends EditorSubComponent {

    /**
     * The last tile cords of the mouse pos.
     */
    private short mouseX, mouseY;

    public SelectionPanel(Editor editor) {
        super(editor);

        // When the first selected cord is (0,0), updating the selection wouldn't happen without this line
        mouseX = -1;

        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        Graphics2D g = (Graphics2D) gr;

        g.setColor(Constants.COLORSELECTION);
        g.draw(new Rectangle2D.Float(0.5f, 0.5f, Editor.SIZE, Editor.SIZE));
    }

    public void onSelectionUpdated(short curX, short curY) {

        float scale = editor.getScale();

        curX = (short) (curX / scale / Editor.SIZE);
        curY = (short) (curY / scale / Editor.SIZE);

        if ((curX == mouseX && curY == mouseY) ||
                curX < 0 || curX >= width ||
                curY < 0 || curY >= height)
            return;

        mouseX = curX;
        mouseY = curY;

        selectCord(curX, curY);

        Cord3S cord = getCords(mouseX, mouseY);
        worldController.onSelectionUpdated(cord, editor);
    }

    private void selectCord(short x, short y) {

        setBounds(x * Editor.SIZE, y * Editor.SIZE,
                (x + 1) * (Editor.SIZE + 1), (y + 1) * (Editor.SIZE + 1));
    }
}
