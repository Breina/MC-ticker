import presentation.controllers.MainController;

import java.io.File;

public class StartSim {

	public static void main(String[] args) {

//		Logger logger = LogManager.getRootLogger();
//		logger.warn("BE CAREFUL");
//		logger.printf(Level.ALL, "test");
		
		MainController app = new MainController();

		if (args.length > 0) {

			app.openSchematic(new File(args[0]));

		} else {

			app.openNewWorldDialog();
		}
	}
}