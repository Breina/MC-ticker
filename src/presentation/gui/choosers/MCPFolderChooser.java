package presentation.gui.choosers;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MCPFolderChooser extends JFileChooser {
	private static final long serialVersionUID = 8462800103389729175L;

	public MCPFolderChooser() {
		super();
		
		init();
	}
	
	public MCPFolderChooser(File defaultFolder) {
		super(defaultFolder);
		
		init();
	}
	
	private void init() {
		
		setDialogTitle("Please locate MCP's root folder");
		
		setPreferredSize(new Dimension(720, 540));
		setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
	}
}
