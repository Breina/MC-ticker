package presentation.tools;

import presentation.controllers.MainController;
import presentation.gui.editor.Editor;
import presentation.gui.editor.SelectionPanel;
import presentation.main.Cord2S;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ToolSelect extends Tool implements MouseMotionListener {

    /**
     * True if we're dragging over other tiles
     */
    private boolean dragging;

    /**
     * The editor in which we're dragging
     */
    private Editor draggingEditor;

    /**
     * True if we're enabling the selection, false to remove selections
     */
    private boolean select;

    /**
     * The starting corner of the drag
     */
    private Cord2S prevStartCord;

	public ToolSelect(MainController mainController) {
		super(mainController, "Select", "select.png", true);

        dragging = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
        int modifiers = e.getModifiersEx();

        boolean shift = (MouseEvent.SHIFT_DOWN_MASK & modifiers) == MouseEvent.SHIFT_DOWN_MASK;
        boolean ctrl = (MouseEvent.CTRL_DOWN_MASK & modifiers) == MouseEvent.CTRL_DOWN_MASK;

        Editor editor = (Editor) e.getSource();
        SelectionPanel selectionPanel = editor.getSelectionPanel();

        if (!ctrl)
            selectionPanel.clearSelection();

        select = !selectionPanel.tileSelected(getSelectedCord2D());

        if (shift && prevStartCord != null)

            selectionPanel.selectRegion(prevStartCord, getSelectedCord2D(), select, false);

        else {
            prevStartCord = getSelectedCord2D();
            selectionPanel.selectTile(getSelectedCord2D(), select);
        }

        editor.repaint();

        draggingEditor = editor;
        dragging = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {

        draggingEditor.getSelectionPanel().selectRegion(prevStartCord, getSelectedCord2D(), select, true);
        draggingEditor.repaint();

        dragging = false;
        draggingEditor = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void onSelectionChanged() {

        if (dragging) {
            draggingEditor.getSelectionPanel().selectRegion(prevStartCord, getSelectedCord2D(), select, false);
            draggingEditor.getSelectionPanel().repaint();
        }
	}

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

        Editor editor = (Editor) e.getSource();

        if (editor.getSelectionPanel().isPositionOnBorder(e.getPoint())) {

            editor.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        } else {
            editor.setCursor(Cursor.getDefaultCursor());
        }

    }
}
