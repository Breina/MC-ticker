package presentation.gui.editor;

import presentation.main.Constants;
import presentation.main.Cord2S;
import presentation.main.Cord3S;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class CursorPanel extends EditorSubComponent {

    /**
     * The last tile cords of the mouse pos.
     */
    private short mouseX, mouseY;

    public CursorPanel(Editor editor) {
        super(editor);

        // When the first selected cord is (0,0), updating the selection wouldn't happen without this line
        mouseX = -1;

        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        Graphics2D g = (Graphics2D) gr;

        g.setColor(Constants.COLORCURSOR);
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

        Cord3S cord3D = getCords(mouseX, mouseY);
        Cord2S cord2D = new Cord2S(curX, curY);
        worldController.onSelectionUpdated(cord2D, cord3D, editor);
    }

    private void selectCord(short x, short y) {

        setBounds(x * Editor.SIZE, y * Editor.SIZE,
                (x + 1) * (Editor.SIZE + 1), (y + 1) * (Editor.SIZE + 1));
    }

    public void selectCord(short x, short y, short z) {

        Cord2S cord = getCord(x, y, z);

        setVisible(cord != null);

        if (cord != null)
            selectCord(cord.x, cord.y);
    }

    public void selectCord(Cord3S c) {
        if (c == null)
            setVisible(false);

        else
            selectCord(c.x, c.y, c.z);
    }
}
