package presentation;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import presentation.controllers.MainController;
import presentation.main.Constants;
import logging.Log;

public class RSFrame extends JFrame {
	private static final long serialVersionUID = -5737200021101047512L;
	
	private MainController controller;

	public RSFrame(MainController controller) {
		super(Constants.APPLICATIONTITLE);
		setExtendedState(MAXIMIZED_BOTH);
		setSize(1200, 800);
		
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