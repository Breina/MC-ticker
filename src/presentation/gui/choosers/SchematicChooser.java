package presentation.gui.choosers;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SchematicChooser extends JFileChooser {
	private static final long serialVersionUID = 8462800103389729175L;

	public SchematicChooser() {
		super();
		
		init();
	}
	
	public SchematicChooser(File defaultFolder) {
		super(defaultFolder);
		
		init();
	}
	
	private void init() {
		
		setDialogTitle("Schematic");
		
		setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Minecraft schematic files (.schematic)";
			}
			
			@Override
			public boolean accept(File f) {
                return (f.isDirectory()) || (f.getName().toLowerCase().endsWith("schematic"));
            }
		});
	}
}
