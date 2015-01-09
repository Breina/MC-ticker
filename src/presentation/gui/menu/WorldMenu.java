package presentation.gui.menu;

import presentation.controllers.WorldController;
import presentation.objects.Orientation;

import javax.swing.*;

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
		top.addActionListener(e -> {
            controller.addNewPerspective(Orientation.TOP);
        });
		
		front.addActionListener(e -> {
            controller.addNewPerspective(Orientation.FRONT);
        });
		
		right.addActionListener(e -> {
            controller.addNewPerspective(Orientation.RIGHT);
        });
		
		save.addActionListener(ae -> {
            controller.save();
        });
		
		saveAs.addActionListener(e -> {
            controller.saveAs();
        });
		
		revert.addActionListener(e -> {

            int reply = JOptionPane.showConfirmDialog(controller.getMainController().getRSframe(),
                    "Are you sure you want to revert all changes?", "Revert changes", JOptionPane.YES_NO_OPTION);

            if (reply == JOptionPane.YES_OPTION) {
                controller.revert();
            }

        });
		
		closeAll.addActionListener(e -> {
            controller.close();
        });
	}
	
	/**
	 * Hides or unhides a linked window
	 */
	public void addLinkedCheckbox(LinkedCheckbox checkbox) {
		add(checkbox);
	}
}
