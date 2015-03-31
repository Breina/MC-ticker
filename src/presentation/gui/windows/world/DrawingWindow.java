package presentation.gui.windows.world;

import presentation.controllers.WorldController;
import presentation.gui.editor.Editor;
import presentation.gui.windows.InternalWindow;
import presentation.objects.Orientation;
import presentation.objects.ViewData;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.*;

public class DrawingWindow extends InternalWindow {
	private static final long serialVersionUID = 3840583251430475315L;
	
	private String title;
	private int max;
	
	private final WorldController worldController;

    private Editor editor;
	
	private JButton btnUp, btnDown, btnZoomIn, btnZoomOut;

	public DrawingWindow(JDesktopPane parent, WorldController controller, Orientation orientation) {
		super(parent, "Loading window...", true);
		
		this.worldController = controller;
		
		ViewData viewData = controller.getWorldData();

        setFrameIcon(new ImageIcon("img/editor/frameIcon.png"));
		JMenuBar menuBar = new JMenuBar();
		
		switch (orientation) {
			case TOP:
				title = viewData.getName() + ": y=";
				max = viewData.getYSize();
				btnUp = new JButton("↑");		// \u2191
				btnDown = new JButton("↓");	// \u2193
				
				break;
				
			case FRONT:
				title = viewData.getName() + ": z=";
				max = viewData.getZSize();
				btnUp = new JButton("↙");		// \u2199
				btnDown = new JButton("↗");	// \u2197
				break;
				
			case RIGHT:
				title = viewData.getName() + ": x=";
				max = viewData.getXSize();
				btnUp = new JButton("→");		// \u2192
				btnDown = new JButton("←");	// \u2190
				break;
				
			default:
				throw new InternalError("Undefined DrawingWindow type");
		}
		
		menuBar.add(btnDown);
		menuBar.add(btnUp);

		updateTitle((short) 0);
		
		// TODO: This assumes layer starts at 0, if it's ever different, fix this.
		btnDown.setEnabled(false);
		if (max == 1)
			btnUp.setEnabled(false);
			
		LayerHandler layerHandler = new LayerHandler();
		btnUp.addActionListener(layerHandler);
		btnDown.addActionListener(layerHandler);

        btnZoomIn = new JButton(new ImageIcon("img/editor/zoomIn.png"));
        btnZoomOut = new JButton(new ImageIcon("img/editor/zoomOut.png"));

        ZoomHandler zoomHandler = new ZoomHandler();
		btnZoomIn.addActionListener(zoomHandler);
		btnZoomOut.addActionListener(zoomHandler);
		
		menuBar.add(btnZoomIn);
		menuBar.add(btnZoomOut);
		
		setJMenuBar(menuBar);
		
		Editor editor = new Editor(controller, this, orientation);
        this.editor = editor;
		add(new JScrollPane(editor));
		
		addInternalFrameListener(new InternalFrameHandler());
        editor.addMouseWheelListener(new ScrollHandler());

		pack();
		
		setLocation(164, 109);
	}

    /**
	 * Handler for the zoom function
	 */
    private class ZoomHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {			
			boolean zoomIn;

            if (ae.getSource() == btnZoomIn)
                zoomIn = true;
            else if (ae.getSource() == btnZoomOut)
                zoomIn = false;
            else
                return;

            zoom(zoomIn);
        }
	}

	/**
	 * Handler for the layer switching.
	 */
    private class LayerHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
            moveLayer(ae.getSource().equals(btnUp));
		}
	}

    private void zoom(boolean in) {
        editor.setScale(editor.getScale() * (in ? 1.25f : 0.8f));
        pack();
    }

    private void moveLayer(boolean up) {

        short layer = editor.getLayerHeight();

        if (up) {
            if (layer == max - 1)
                return;

            layer++;
            if (layer == max - 1)
                btnUp.setEnabled(false);
            if (!btnDown.isEnabled())
                btnDown.setEnabled(true);

        } else {
            if (layer == 0)
                return;

            layer--;
            if (layer == 0)
                btnDown.setEnabled(false);
            if (!btnUp.isEnabled())
                btnUp.setEnabled(true);
        }

        editor.setLayerHeight(layer);
        updateTitle(layer);
    }

	/**
	 * Updates the title to display a new layer number.
	 */
	private void updateTitle(short layer) {
		setTitle(this.title + layer);
	}

	public Editor getEditor() {
		return editor;
	}
	
    private class InternalFrameHandler extends InternalFrameAdapter {
		@Override
		public void internalFrameClosing(InternalFrameEvent e) {
			worldController.drawingWindowClosed((DrawingWindow) e.getSource());
		}    	
    }

    private class ScrollHandler implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

            int modifiers = e.getModifiersEx();
            boolean ctrl = (MouseEvent.CTRL_DOWN_MASK & modifiers) == MouseEvent.CTRL_DOWN_MASK;
            boolean up = e.getWheelRotation() == -1;

            if (!ctrl)
                moveLayer(up);
            else
                zoom(up);
        }
    }

    public int getMax() {
        return max;
    }
}
