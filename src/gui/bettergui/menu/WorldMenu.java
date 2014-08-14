package gui.bettergui.menu;

import gui.bettergui.choosers.SchematicChooser;
import gui.controllers.WorldController;
import gui.exceptions.SchematicException;
import gui.objects.Orientation;
import gui.objects.ViewData;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import logging.Log;

/**
 * A menu for showing a World's actions
 */
public class WorldMenu extends JMenu {
	private static final long serialVersionUID = 6210179007123517451L;
	
	private WorldController controller;
	
	public WorldMenu(WorldController controller) {
		super(controller.getWorldData().getName());
		
		this.controller = controller;
		
		buildGUI();
	}
	
	public void buildGUI() {
		
		// Build layout
		JMenuItem top		= new JMenuItem("Add top-down view");
		JMenuItem front		= new JMenuItem("Add front view");
		JMenuItem right		= new JMenuItem("Add right view");
		top					.setMnemonic('t');
		front				.setMnemonic('f');
		right				.setMnemonic('r');
		add(top);
		add(front);
		add(right);
		
		add(new JSeparator());
		
		JMenuItem save		= new JMenuItem("Save");
		JMenuItem saveAs	= new JMenuItem("Save As...");
		JMenuItem revert	= new JMenuItem("Revert");
		save				.setMnemonic('S');
		saveAs				.setMnemonic('A');
		revert				.setMnemonic('e');
		add(save);
		add(saveAs);
		add(revert);
		
		add(new JSeparator());
		
		JMenuItem closeAll	= new JMenuItem("Close all");
		closeAll			.setMnemonic('C');
		add(closeAll);
		
		add(new JSeparator());
		
		// Add functions
		top.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.addNewPerspective(Orientation.TOP);
			}
		});
		
		front.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.addNewPerspective(Orientation.FRONT);
			}
		});
		
		right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.addNewPerspective(Orientation.RIGHT);
			}
		});
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {

				controller.getWorldData().save();	
			}
		});
		
		saveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
//				SchematicChooser chooser = new SchematicChooser(controller.getWorldData().getSchematicFile());
//				
//				int result = chooser.showSaveDialog(controller.getMainController().getRSframe());
//				
//				if (result == SchematicChooser.APPROVE_OPTION) {
//					
//					
//					ViewData viewData = controller.getWorldData();
//					viewData.setSchematicFile(chooser.getSelectedFile());
//					viewData.save();
//					
//				} else if (result == SchematicChooser.ERROR_OPTION)
//					Log.e("Something when wrong when selecting a file to save to.");
			}
		});
		
		revert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int reply = JOptionPane.showConfirmDialog(controller.getMainController().getRSframe(),
						"Are you sure you want to revert all changes?", "Revert changes", JOptionPane.YES_NO_OPTION);
				
				if (reply == JOptionPane.YES_OPTION) {
					controller.revert();
				}
				
			}
		});
		
		closeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.close();
			}
		});
	}
	
	/**
	 * Hides or unhides a linked window
	 */
	public void addLinkedCheckbox(LinkedCheckbox checkbox) {
		add(checkbox);
	}
}
