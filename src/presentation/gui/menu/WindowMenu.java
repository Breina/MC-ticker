package presentation.gui.menu;

import presentation.controllers.MainController;

import javax.swing.*;

public class WindowMenu extends JMenu {
	private static final long serialVersionUID = 9058054431044220813L;

	private final MainController mainController;
	
	private JMenuItem emptyWorldEntry;
	private int openWorlds;	
	
	public WindowMenu(MainController mainController) {
		super("Window");
		
		this.mainController = mainController;
		this.openWorlds = 0;
		
		buildGUI();
	}
	
	void buildGUI() {
		
		setMnemonic('W');
		
		emptyWorldEntry = new JMenuItem("No active schematics");
		emptyWorldEntry.setEnabled(false);
		add(emptyWorldEntry);
		
		add(new JSeparator());
	}
	
	public void addWorldMenu(WorldMenu worldMenu) {
		
		emptyWorldEntry.setVisible(false);
		openWorlds++;
		
		add(worldMenu, 0);		
	}
	
	public void removeWorldMenu(WorldMenu worldMenu) {
		
		remove(worldMenu);
		
		if (--openWorlds == 0)
			emptyWorldEntry.setVisible(true);
	}
	
	/**
	 * Hides or unhides a linked window
	 */
	public void addLinkedCheckbox(LinkedCheckbox checkbox) {
		add(checkbox);
	}
}
