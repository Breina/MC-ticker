package gui.bettergui;

import gui.bettergui.editor.EditorPanel;
import gui.controllers.WorldController;
import gui.objects.Orientation;
import gui.objects.WorldData;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class DrawingWindow extends InternalWindow {
	private static final long serialVersionUID = 3840583251430475315L;
	
	private String title;
	private int max;
	
	private WorldController worldController;
	
	private EditorPanel editor;
	
	private JButton up, down;

	/**
	 * Instances a new drawing window.
	 * @param orientation The perspective that's being drawn; TOP, RIGHT or FRONT.
	 * @param levelOLD A reference to the level.
	 */
	public DrawingWindow(WorldController controller, Orientation orientation) {
		super(controller.getMainController(), "Loading window...");
		
		this.worldController = controller;
		
		WorldData worldData = controller.getWorldData();
		
		JMenuBar menuBar = new JMenuBar();
		
		switch (orientation) {
			case TOP:
				title = worldData.getName() + ": y=";
				max = worldData.getYSize();
				up = new JButton("↑");		// \u2191
				down = new JButton("↓");	// \u2193
				
				break;
				
			case FRONT:
				title = worldData.getName() + ": z=";
				max = worldData.getZSize();
				up = new JButton("↙");		// \u2199
				down = new JButton("↗");	// \u2197
				break;
				
			case RIGHT:
				title = worldData.getName() + ": x=";
				max = worldData.getXSize();
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
		layerHandler = null;		
		
		JButton zoomIn = new JButton("+");
		JButton zoomOut = new JButton("-");
		
		ZoomHandler zoomHandler = new ZoomHandler();
		zoomIn.addActionListener(zoomHandler);
		zoomOut.addActionListener(zoomHandler);
		zoomHandler = null;
		
		menuBar.add(zoomIn);
		menuBar.add(zoomOut);
		
		setJMenuBar(menuBar);
		
		editor = new EditorPanel(controller, orientation);
		add(new JScrollPane(editor));
		
		addInternalFrameListener(new InternalFrameHandler());
		
		// TODO Update layer markers
//		updateLayerOnAllWindows();
		
		pack();
		
//		editor.repaintAll();
	}
	
	/**
	 * Handler for the zoom function
	 */
	public class ZoomHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {			
			JButton btn = (JButton) ae.getSource();
			boolean b = btn.getText().equals("+");
			btn = null;
			
			editor.setScale(editor.getScale() * (b ? 1.25f : 0.8f));
			
			pack();
		}
	}

	/**
	 * Handler for the layer switching.
	 */
	public class LayerHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			
			short layer = editor.getLayer();
			
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
			
			editor.setLayer(layer); // Also repaints
			updateTitle(layer);
			
			worldController.updateLayers(getEditor());
		}
	}

	/**
	 * Updates the title to display a new layer number.
	 */
	private void updateTitle(short layer) {
		setTitle(this.title + layer);
	}

	public EditorPanel getEditor() {
		return editor;
	}
	
    public class InternalFrameHandler extends InternalFrameAdapter {
		@Override
		public void internalFrameClosing(InternalFrameEvent e) {
			worldController.drawingWindowClosed((DrawingWindow) e.getSource());
		}    	
    }
}