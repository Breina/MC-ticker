package presentation.tools;

import logging.Log;
import presentation.controllers.MainController;
import presentation.gui.editor.Editor;
import presentation.gui.editor.selection.SelectionManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ToolSelect extends Tool implements MouseMotionListener {

    /**
     * True if we're dragging over other tiles
     */
    private boolean dragging;

    /**
     * Keeps track of which selection manager (thus which world) we're dragging
     */
    private SelectionManager prevSelection;

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

        SelectionManager selection = getWorldController().getSelectionManager();

        selection.startSelection(getSelectedCord3D(), shift, ctrl);

//        if (!ctrl)
//            selection.clearSelection();
//
//        Cord3S c = getSelectedCord3D();
//
//        if (shift && prevStartCord != null && selection == prevSelection) {
//
//            selection.selectRegion(prevStartCord, c);
//
//        } else {
//            prevStartCord = c;
//            selection.selectPoint(c);
//        }

        prevSelection = selection;
        dragging = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {

        dragging = false;

        if (getWorldController().getSelectionManager() != prevSelection) {
            Log.w("The selection drag started on a different schematic than it ended, selecting nothing.");
            return;
        }

        prevSelection.endSelection();

//        prevSelection.selectRegion(prevStartCord, getSelectedCord3D());
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

            if (getWorldController().getSelectionManager() != prevSelection) {
                Log.w("The selection is dragging through another selection, selecting nothing.");
                return;
            }

            prevSelection.dragSelection(getSelectedCord3D());
        }
	}

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

        Editor editor = (Editor) e.getSource();

        if (editor.getSelectionPanel().isPositionOnBorder(e.getPoint()))

            editor.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        else

            editor.setCursor(Cursor.getDefaultCursor());

    }
}
