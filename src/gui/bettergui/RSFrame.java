package gui.bettergui;

import java.awt.BorderLayout;

import gui.controllers.MainController;
import gui.main.Constants;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import logging.Log;

public class RSFrame extends JFrame {
	private static final long serialVersionUID = -5737200021101047512L;
	
	private MainController controller;

	public RSFrame(MainController controller) {
		super(Constants.APPLICATIONTITLE);
		setExtendedState(MAXIMIZED_BOTH);
		setSize(1024, 1024);
		
		this.controller = controller;

		buildGUI();
	}

	/**
	 * Builds the GUI.
	 */
	private void buildGUI() {

		setLayout(new BorderLayout());
		final DesktopPane desktop = controller.getDesktop();
		add(desktop, BorderLayout.CENTER);
		
		add(controller.getStatusPanel(), BorderLayout.SOUTH);

		Log.i("Logwindow loaded.");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		menuBar.add(controller.getFileMenu());
		menuBar.add(controller.getWindowMenu());

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}