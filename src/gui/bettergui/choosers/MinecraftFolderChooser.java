package gui.bettergui.choosers;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;

public class MinecraftFolderChooser extends JFileChooser {
	private static final long serialVersionUID = 8462800103389729175L;

	public MinecraftFolderChooser() {
		super();
		
		init();
	}
	
	public MinecraftFolderChooser(File defaultFolder) {
		super(defaultFolder);
		
		init();
	}
	
	private void init() {
		
		setDialogTitle("Please locate the .minecraft folder");
		setPreferredSize(new Dimension(720, 540));
		setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
}
