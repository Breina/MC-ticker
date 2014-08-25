package gui;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import logging.Log;

public class Util {

	public static ImageIcon getIcon(String path) {
		try {
			return new ImageIcon(ImageIO.read(new File("img/" + path)));
		} catch (IOException e) {
			Log.e("Failed to read icon " + path + ": " + e.getMessage());
			return null;
		}
	}
}
