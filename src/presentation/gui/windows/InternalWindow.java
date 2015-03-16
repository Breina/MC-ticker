package presentation.gui.windows;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class InternalWindow extends JInternalFrame {

    private DesktopManager desktopManager;

    public InternalWindow(JDesktopPane parent, String title, boolean visible) {
        super(title, true, true, true, true);

        this.desktopManager = parent.getDesktopManager();

        addComponentListener(new ResizeHandler());

        parent.add(this);
        setVisible(visible);
    }

    private class ResizeHandler extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            JInternalFrame f = (JInternalFrame) e.getSource();
            desktopManager.setBoundsForFrame(f, f.getX(), f.getY(), f.getWidth(), f.getHeight());
        }
    }
}
