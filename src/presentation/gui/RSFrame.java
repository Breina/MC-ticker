package presentation.gui;

import logging.Log;
import presentation.controllers.MainController;
import presentation.gui.toolbar.WrappingLayout;
import presentation.main.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

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

        try {
            setIconImage(ImageIO.read(new File("img/MCsim.png")));

        } catch (IOException e) {
            Log.w("Could not set application icon: " + e.getMessage());
        }

        setLayout(new BorderLayout());
		DesktopPane desktop = controller.getDesktop();
		add(desktop, BorderLayout.CENTER);
		
		add(controller.getStatusPanel(), BorderLayout.SOUTH);
		add(controller.getBlockPanel(), BorderLayout.WEST);

		JPanel toolbar = new JPanel(new WrappingLayout(WrappingLayout.LEFT));

		toolbar.add(controller.getToolbar());
		toolbar.add(controller.getTimebar());

		add(toolbar, BorderLayout.NORTH);

		Log.i("Logwindow loaded.");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		menuBar.add(controller.getFileMenu());
		menuBar.add(controller.getWindowMenu());

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}