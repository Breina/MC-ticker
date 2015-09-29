package presentation.gui.editor;

import presentation.controllers.MainController;
import presentation.gui.windows.main.options.IPreferenceChangedListener;
import presentation.main.Constants;
import presentation.main.Cord2S;
import presentation.main.Cord3S;
import sim.constants.Prefs;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.prefs.Preferences;

class CursorPanel extends EditorSubComponent implements IPreferenceChangedListener {

    /**
     * The last tile cords of the mouse pos.
     */
    private short mouseX, mouseY;

    private Color cursorColor;

    public CursorPanel(MainController controller, Editor editor) {
        super(editor);

        controller.getOptionsController().registerPreferenceListener(Prefs.EDITOR_COLOR_CURSOR, this);
        cursorColor = new Color(Preferences.userRoot().getInt(Prefs.EDITOR_COLOR_CURSOR, Constants.COLORCURSOR.getRGB()), true);

        // When the first selected cord is (0,0), updating the selection wouldn't happen without this line
        mouseX = -1;

        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;

        g.setColor(cursorColor);
        g.draw(new Rectangle2D.Float(0.5f, 0.5f, Editor.SIZE, Editor.SIZE));

        super.paintComponent(gr);
    }

    public void onSelectionUpdated(short curX, short curY) {

        float scale = editor.getScale();

        curX = (short) (curX / scale / Editor.SIZE);
        curY = (short) (curY / scale / Editor.SIZE);

        if ((curX == mouseX && curY == mouseY) ||
                curX < 0 || curX >= editorWidth ||
                curY < 0 || curY >= editorHeight)
            return;

        mouseX = curX;
        mouseY = curY;

        selectCord(curX, curY);

        Cord3S cord3D = getCord3D(mouseX, mouseY);
        Cord2S cord2D = new Cord2S(curX, curY);
        worldController.onSelectionUpdated(cord2D, cord3D, editor);

        worldController.repaintAllEditors();
    }

    private void selectCord(short x, short y) {

        setBounds(x * Editor.SIZE, y * Editor.SIZE,
                (x + 1) * (Editor.SIZE + 1), (y + 1) * (Editor.SIZE + 1));
    }

    void selectCord(short x, short y, short z) {

        Cord2S cord = getCord2D(x, y, z);

        setVisible(cord != null);

        if (cord != null)
            selectCord(cord.x, cord.y);
    }

    public void selectCord(Cord3S c) {
        if (c == null) {
            setVisible(false);
            mouseX = -1;
            mouseY = -1;

        } else
            selectCord(c.x, c.y, c.z);

        // TODO maybe not repaint everything, ever, but repaint the region around it orso
        worldController.repaintAllEditors();
    }

    @Override
    public void preferenceChanged(String preference) {
        cursorColor = new Color(Preferences.userRoot().getInt(Prefs.EDITOR_COLOR_CURSOR, Constants.COLORCURSOR.getRGB()), true);
    }
}
