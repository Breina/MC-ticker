package gui.bettergui.windows.main;

import gui.bettergui.DesktopPane;
import gui.controllers.MainController;

import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

public class ToolWindow extends MainWindow {
	private static final long serialVersionUID = 5371809301081276026L;
	
	private JToggleButton btnActivate, btnPlace, btnSelect, btnRotate, btnMove;

	public ToolWindow(MainController controller) {
		super(controller, "Tools", true);
		
		setLocation(0, 0);
		
		buildGUI();
	}
	
	public void buildGUI() {
		
		setLayout(new GridLayout(1, 5));
		
		add(btnActivate = new JToggleButton("Activate"));
		add(btnPlace = new JToggleButton("Place"), true);
		add(btnSelect = new JToggleButton("Select"));
		add(btnRotate = new JToggleButton("Rotate"));
		add(btnMove = new JToggleButton("Move"));
		
		ButtonGroup group = new ButtonGroup();
		group.add(btnActivate);
		group.add(btnPlace);
		group.add(btnSelect);
		group.add(btnRotate);
		group.add(btnMove);
		
		pack();
		
		setLayer(DesktopPane.PALETTE_LAYER);
	}
}
