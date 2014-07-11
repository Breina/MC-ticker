package gui;

import gui.controllers.MainController;

public class StartSim {

	public static void main(String[] args) {
		
//		Logger logger = LogManager.getRootLogger();
//		logger.warn("BE CAREFUL");
//		logger.printf(Level.ALL, "test");
		
		new MainController();
	}
}
