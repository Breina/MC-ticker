package presentation.gui.desktop;

import javax.swing.*;
import java.awt.*;

/**
 * Will keep the internal frames inside the desktop frame.
 *
 * http://stackoverflow.com/questions/8136944/preventing-jinternalframe-from-being-moved-out-of-a-jdesktoppane
 */
public class BoundedDesktopManager extends DefaultDesktopManager {

    @Override
    public void beginDraggingFrame(JComponent f) {
        // Don't do anything. Needed to prevent the DefaultDesktopManager setting the dragMode
    }

    @Override
    public void beginResizingFrame(JComponent f, int direction) {
        // Don't do anything. Needed to prevent the DefaultDesktopManager setting the dragMode
    }

    @Override
    public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
        if (!inBounds((JInternalFrame) f, newX, newY, newWidth, newHeight)) {
            Container parent = f.getParent();
            Dimension parentSize = parent.getSize();

            if (parentSize.getWidth() < newWidth)
                newWidth = (int) parentSize.getWidth();

            if (parentSize.getHeight() < newHeight)
                newHeight = (int) parentSize.getHeight();

            int boundedX = (int) Math.max(0, Math.min(Math.max(0, newX), parentSize.getWidth() - newWidth));
            int boundedY = (int) Math.max(0, Math.min(Math.max(0, newY), parentSize.getHeight() - newHeight));

//            boundedX = (int) Math.max(0, parentSize.getWidth() - newWidth)
            f.setBounds(boundedX, boundedY, newWidth, newHeight);
        } else {
            f.setBounds(newX, newY, newWidth, newHeight);
        }
        if(didResize) {
            f.validate();
        }
    }

    protected boolean inBounds(JInternalFrame f, int newX, int newY, int newWidth, int newHeight) {
        if (newX < 0 || newY < 0) return false;
        if (newX + newWidth > f.getDesktopPane().getWidth()) return false;
        if (newY + newHeight > f.getDesktopPane().getHeight()) return false;
        return true;
    }
}
