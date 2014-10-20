package sim.constants;

import java.io.File;

import javax.swing.JFrame;

import logging.Log;
import presentation.gui.choosers.MCPFolderChooser;
import presentation.gui.choosers.MinecraftFolderChooser;

public class Globals {
	
	public static String getMinecraftFolder() {
		
		return getMinecraftFolder(null);
	}

	public static String getMinecraftFolder(JFrame parent) {
		// Dumb windows attempt
		String minecraftFolder = System.getenv("APPDATA") + sim.constants.Constants.MINECRAFTFOLDER;
		
		if (!new File(minecraftFolder).exists())
			minecraftFolder = "minecraft";

		if (!new File(minecraftFolder).exists()) {
			
			MinecraftFolderChooser minecraftDialog = new MinecraftFolderChooser();
			int result = minecraftDialog.showDialog(parent, "Select");
			
			if (result != MinecraftFolderChooser.APPROVE_OPTION)
				if (result == MinecraftFolderChooser.ERROR_OPTION)
					Log.e("Something went wrong when finding .minecraft folder.");
			
			minecraftFolder = minecraftDialog.getSelectedFile().getAbsolutePath();
		}
		
		return minecraftFolder;
	}
}
