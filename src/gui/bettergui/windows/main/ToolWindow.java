package gui.bettergui.windows.main;

import gui.Util;
import gui.bettergui.DesktopPane;
import gui.bettergui.editor.EditorPanel;
import gui.bettergui.windows.world.DrawingWindow;
import gui.controllers.MainController;
import gui.controllers.WorldController;
import gui.tools.Tool;
import gui.tools.ToolActivate;
import gui.tools.ToolPlace;
import gui.tools.ToolRotate;
import gui.tools.ToolSelect;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import logging.Log;

public class ToolWindow extends MainWindow {
	private static final long serialVersionUID = 5371809301081276026L;
	
	private Tool currentTool;
	private MainController mainController;

	public ToolWindow(MainController mainController) {
		super(mainController, "Tools", true);
		
		setFrameIcon(Util.getIcon("tools/toolbox.png"));
		
		this.mainController = mainController;
		
		setLocation(0, 0);
		
		buildGUI();
	}
	
	public void buildGUI() {
		
		setLayout(new GridLayout(1, 5));

		ButtonGroup group = new ButtonGroup();
		addTool(group, new ToolActivate(mainController));
		addTool(group, new ToolSelect(mainController));
		addTool(group, new ToolPlace(mainController));
		addTool(group, new ToolRotate(mainController));
		
		pack();
		
		setLayer(DesktopPane.PALETTE_LAYER);
	}
	
	private void addTool(ButtonGroup group, final Tool tool) {
		JToggleButton btn;
		
		if (tool.getFileName() == null)
			btn = new JToggleButton(tool.getName());
		else {
			btn = new JToggleButton(Util.getIcon("tools/" + tool.getFileName()));
			btn.setToolTipText(tool.getName());
		}
			
		add(btn);
		group.add(btn);
		
		if (group.getSelection() == null) {
			btn.setSelected(true);
			selectTool(tool);
		}
		
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTool(tool);
			}
		});
	}
	
	public void selectTool(Tool tool) {
		for (WorldController worldController : mainController.getWorldControllers())
			for (DrawingWindow window : worldController.getOpenWindows()) {
				EditorPanel editor = window.getEditor();
				
				editor.removeMouseListener(currentTool);
				if (currentTool.hasMouseMotionListener())
					editor.removeMouseMotionListener((MouseMotionListener) currentTool);
				
				editor.addMouseListener(tool);
				if (tool.hasMouseMotionListener())
					editor.addMouseMotionListener((MouseMotionListener) tool);
			}
		
		currentTool = tool;
		mainController.setTool(tool);
	}
}
