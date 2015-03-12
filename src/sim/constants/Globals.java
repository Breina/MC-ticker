package sim.constants;

import logging.Log;
import presentation.gui.choosers.MinecraftFolderChooser;

import javax.swing.*;
import java.io.File;

public class Globals {
	
	public static String getMinecraftFolder() {
		
		return getMinecraftFolder(null);
	}

	private static String getMinecraftFolder(JFrame parent) {
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
