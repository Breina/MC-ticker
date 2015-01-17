package presentation.gui.toolbar;

import presentation.controllers.MainController;
import presentation.controllers.WorldController;

import javax.swing.*;

public class Timebar extends JToolBar {

    MainController mainController;
    WorldController worldController;

    public Timebar(MainController mainController) {
        super("Timebar");

        add(new JLabel("Test"));
    }
}
