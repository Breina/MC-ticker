package presentation.gui;

import logging.Log;
import presentation.StatusPanel;
import presentation.controllers.MainController;
import presentation.gui.menu.FileMenu;
import presentation.gui.menu.WindowMenu;
import presentation.gui.toolbar.Timebar;
import presentation.gui.toolbar.Toolbar;
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
    private StatusPanel statusPanel;
    private DesktopPane desktop;
    private BlockPanel blockPanel;
    private Toolbar toolbar;
    private Timebar timebar;
    private FileMenu fileMenu;
    private WindowMenu windowMenu;

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
		desktop = new DesktopPane();
		add(desktop, BorderLayout.CENTER);

        statusPanel = new StatusPanel();
		add(statusPanel, BorderLayout.SOUTH);

        blockPanel = new BlockPanel(controller);
		add(blockPanel, BorderLayout.WEST);

		JPanel toolbarContainer = new JPanel(new WrappingLayout(WrappingLayout.LEFT));

            toolbar = new Toolbar(controller);
            toolbarContainer.add(toolbar);

            timebar = new Timebar(controller);
            toolbarContainer.add(timebar);

		add(toolbarContainer, BorderLayout.NORTH);

		JMenuBar menuBar = new JMenuBar();

            fileMenu = new FileMenu(controller);
            menuBar.add(fileMenu);

            windowMenu = new WindowMenu(controller);
            menuBar.add(windowMenu);

        setJMenuBar(menuBar);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

    public WindowMenu getWindowMenu() {
        return windowMenu;
    }

    public StatusPanel getStatusPanel() {
        return statusPanel;
    }

    public JDesktopPane getDesktop() {
        return desktop;
    }

    public Timebar getTimebar() {
        return timebar;
    }
}