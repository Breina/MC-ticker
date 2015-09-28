package sim.constants;

public class Prefs {

    public static final String EDITOR_MAXFPS = "editor-maxfps";
    public static final String EDITOR_COLOR_LAYER = "editor-color-layer";
    public static final String EDITOR_COLOR_CURSOR = "editor-color-cursor";
    // TODO create constant preferecnes, look in OptionsWindow and look for usages. Each usage should register itself as
    // a listener mainController.getOptionsWindow.registerListener(preference, this);
    // TODO also use a switch to check for correct prefrence updates so not everything is updated.
}
