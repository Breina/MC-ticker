package presentation.gui.menu;

import logging.Log;
import presentation.controllers.MainController;
import presentation.gui.choosers.SchematicChooser;
import presentation.main.Constants;

import javax.swing.*;
import java.io.File;

public class FileMenu extends JMenu {
	private static final long serialVersionUID = 9058054431044220813L;

	private final MainController controller;
	
	public FileMenu(MainController controller) {
		super("File");
		
		this.controller = controller;
		
		buildGUI();
	}
	
	void buildGUI() {
		
		setMnemonic('F');
		
		JMenuItem newItem		= new JMenuItem("New", new ImageIcon("img/menu/new.png"));
		JMenuItem openItem		= new JMenuItem("Open...", new ImageIcon("img/menu/open.png"));
		JMenuItem saveItem		= new JMenuItem("Save All", new ImageIcon("img/menu/saveAll.png"));
		newItem					.setMnemonic('N');
		openItem				.setMnemonic('O');
		saveItem				.setMnemonic('S');
		add(newItem);
		add(openItem);
		add(saveItem);
		
		add(new JSeparator());

        JMenuItem optionsItem   = new JMenuItem("Options...", new ImageIcon("img/options/gear.png"));
		JMenuItem exportItem	= new JMenuItem("Export...", new ImageIcon("img/export/export.png"));
        optionsItem             .setMnemonic('O');
		exportItem				.setMnemonic('E');
        add(optionsItem);
		add(exportItem);
		
		add(new JSeparator());

		JMenuItem exitItem		= new JMenuItem("Exit", new ImageIcon("img/menu/exit.png"));
		exitItem				.setMnemonic('x');
		add(exitItem);
		
		newItem.addActionListener(e -> SwingUtilities.invokeLater(controller::openNewWorldDialog));

        openItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {

            SchematicChooser chooser = new SchematicChooser(new File(Constants.SCHEMATICSDIR));
            int result = chooser.showOpenDialog(controller.getFrame());

            if (result != SchematicChooser.APPROVE_OPTION) {
                if (result == SchematicChooser.ERROR_OPTION)
                    Log.e("Failed to open schematic.");

                return;
            }

            controller.openSchematic(chooser.getSelectedFile());
        }));

        saveItem.addActionListener(e -> SwingUtilities.invokeLater(controller::saveAll));

        optionsItem.addActionListener(e -> SwingUtilities.invokeLater(controller::showOptions));
        exportItem.addActionListener(e -> SwingUtilities.invokeLater(controller::export));

        exitItem.addActionListener(e -> controller.exit());
    }
}