package presentation.gui.windows.world;

import presentation.controllers.WorldController;
import presentation.gui.editor.Editor;
import presentation.gui.windows.InternalWindow;
import presentation.objects.Orientation;
import presentation.objects.ViewData;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DrawingWindow extends InternalWindow {
	private static final long serialVersionUID = 3840583251430475315L;
	
	private String title;
	private int max;
	
	private final WorldController worldController;

    private Editor editor;
	
	private JButton up, down;

	public DrawingWindow(JDesktopPane parent, WorldController controller, Orientation orientation) {
		super(parent, "Loading window...", true);
		
		this.worldController = controller;
		
		ViewData viewData = controller.getWorldData();
		
		JMenuBar menuBar = new JMenuBar();
		
		switch (orientation) {
			case TOP:
				title = viewData.getName() + ": y=";
				max = viewData.getYSize();
				up = new JButton("↑");		// \u2191
				down = new JButton("↓");	// \u2193
				
				break;
				
			case FRONT:
				title = viewData.getName() + ": z=";
				max = viewData.getZSize();
				up = new JButton("↙");		// \u2199
				down = new JButton("↗");	// \u2197
				break;
				
			case RIGHT:
				title = viewData.getName() + ": x=";
				max = viewData.getXSize();
				up = new JButton("→");		// \u2192
				down = new JButton("←");	// \u2190
				break;
				
			default:
			case UNDEFINED:
				throw new InternalError("Undefined DrawingWindow type");
		}
		
		menuBar.add(down);
		menuBar.add(up);

		updateTitle((short) 0);
		
		// TODO: This assumes layer starts at 0, if it's ever different, fix this.
		down.setEnabled(false);
		if (max == 1) {
			up.setEnabled(false);
		}
			
		LayerHandler layerHandler = new LayerHandler();
		up.addActionListener(layerHandler);
		down.addActionListener(layerHandler);
		
		JButton zoomIn = new JButton("+");
		JButton zoomOut = new JButton("-");
		
		ZoomHandler zoomHandler = new ZoomHandler();
		zoomIn.addActionListener(zoomHandler);
		zoomOut.addActionListener(zoomHandler);
		
		menuBar.add(zoomIn);
		menuBar.add(zoomOut);
		
		setJMenuBar(menuBar);
		
		Editor editor = new Editor(controller, this, orientation);
        this.editor = editor;
		add(new JScrollPane(editor));
		
		addInternalFrameListener(new InternalFrameHandler());

		pack();
		
		setLocation(164, 109);
	}

    /**
	 * Handler for the zoom function
	 */
    private class ZoomHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {			
			JButton btn = (JButton) ae.getSource();
			boolean b = btn.getText().equals("+");

            editor.setScale(editor.getScale() * (b ? 1.25f : 0.8f));

            pack();
        }
	}

	/**
	 * Handler for the layer switching.
	 */
    private class LayerHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			
			short layer = editor.getLayerHeight();
			
			if (ae.getSource().equals(up)) {
				
				layer++;
				if (layer == max - 1)
					up.setEnabled(false);
				if (!down.isEnabled())
					down.setEnabled(true);
				
			} else {
				
				layer--;
				if (layer == 0)
					down.setEnabled(false);
				if (!up.isEnabled())
					up.setEnabled(true);
			}
			
			editor.setLayerHeight(layer);
			updateTitle(layer);
		}
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

    public int getMax() {
        return max;
    }
}
