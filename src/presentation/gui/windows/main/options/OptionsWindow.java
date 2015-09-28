package presentation.gui.windows.main.options;

import presentation.controllers.MainController;
import presentation.gui.windows.InternalWindow;
import presentation.main.Constants;
import sim.constants.Prefs;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.prefs.Preferences;

public class OptionsWindow extends InternalWindow {

    private Preferences prefs;
    private OptionsController optionsController;

    public OptionsWindow(MainController controller) {

        super(controller.getFrame().getDesktop(), "Options", true);

        this.optionsController = controller.getOptionsController();

        prefs = Preferences.userRoot();

        setLocation(200, 200);
        setSize(250, 250);

        buildGUI();

        setVisible(true);
        setPreferredSize(new Dimension(250, 250));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void buildGUI() {

        setFrameIcon(new ImageIcon("img/options/gear.png"));

        JTabbedPane tabs = new JTabbedPane();
        setContentPane(tabs);

            JPanel pnlEditor = new JPanel();
            pnlEditor.setLayout(new BoxLayout(pnlEditor, BoxLayout.Y_AXIS));
            tabs.add("Editor", pnlEditor);

                JPanel pnlDefaultZoom = new JPanel(new BorderLayout(5, 5));
                pnlEditor.add(pnlDefaultZoom);
                pnlDefaultZoom.add(new JLabel("Default zoom"));
                JSpinner spinnerDefaultZoom = new JSpinner(new SpinnerNumberModel(
                        prefs.getFloat("editor-defaultzoom", Constants.DEFAULTZOOM), 0f, 100f, 0.1f)
                );
                spinnerDefaultZoom.addChangeListener(e -> {     // Java you moron -.-
                            prefs.putFloat("editor-defaultzoom", (float) ((double) spinnerDefaultZoom.getValue()));
                        });
                pnlDefaultZoom.add(spinnerDefaultZoom, BorderLayout.EAST);

                pnlEditor.add(Box.createVerticalStrut(5));

                JPanel pnlMaxFPS = new JPanel(new BorderLayout(5, 5));
                pnlEditor.add(pnlMaxFPS);
                pnlMaxFPS.add(new JLabel("Max FPS"));
                JSpinner spinnerMaxFPS = new JSpinner(new SpinnerNumberModel(
                        prefs.getInt(Prefs.EDITOR_MAXFPS, Constants.MAX_FPS), 1, 1000, 1)
                );
                spinnerMaxFPS.addChangeListener(e -> {
                    prefs.putInt(Prefs.EDITOR_MAXFPS, (Integer) spinnerMaxFPS.getValue());
                    optionsController.notifyListeners(Prefs.EDITOR_MAXFPS);
                });
                pnlMaxFPS.add(spinnerMaxFPS, BorderLayout.EAST);

                pnlEditor.add(Box.createVerticalStrut(5));

                JPanel pnlColors = new JPanel(new GridLayout(6, 1, 5, 5));
                pnlColors.setPreferredSize(new Dimension(200, 150));
                pnlEditor.add(pnlColors);
                pnlColors.setBorder(BorderFactory.createTitledBorder("Colors"));

                    JPanel pnlColorLayer = new JPanel(new BorderLayout());
                    pnlColors.add(pnlColorLayer);
                    pnlColorLayer.add(new JLabel("Layer"), BorderLayout.WEST);
                    pnlColorLayer.add(new ColorPickerButton("Layer",
                            prefs.getInt(Prefs.EDITOR_COLOR_LAYER, Constants.COLORACTIVELAYER.getRGB())) {
                        @Override
                        public void colorUpdated(Color color) {
                            prefs.putInt(Prefs.EDITOR_COLOR_LAYER, color.getRGB());
                            optionsController.notifyListeners(Prefs.EDITOR_COLOR_LAYER);
                        }
                    },  BorderLayout.EAST);

                    JPanel pnlColorCursor = new JPanel(new BorderLayout());
                    pnlColors.add(pnlColorCursor);
                    pnlColorCursor.add(new JLabel("Cursor"), BorderLayout.WEST);
                    pnlColorCursor.add(new ColorPickerButton("Cursor",
                            prefs.getInt("editor-color-cursor", Constants.COLORCURSOR.getRGB())) {
                        @Override
                        public void colorUpdated(Color color) {
                            prefs.putInt("editor-color-cursor", color.getRGB());
                        }
                    },  BorderLayout.EAST);

                    JPanel pnlColorEntity = new JPanel(new BorderLayout());
                    pnlColors.add(pnlColorEntity);
                    pnlColorEntity.add(new JLabel("Entity"), BorderLayout.WEST);
                    pnlColorEntity.add(new ColorPickerButton("Entity",
                            prefs.getInt("editor-color-entity", Color.RED.getRGB())) {
                        @Override
                        public void colorUpdated(Color color) {
                            prefs.putInt("editor-color-entity", color.getRGB());
                        }
                    },  BorderLayout.EAST);

                    JPanel pnlColorEntitySpeed = new JPanel(new BorderLayout());
                    pnlColors.add(pnlColorEntitySpeed);
                    pnlColorEntitySpeed.add(new JLabel("Entity speed"), BorderLayout.WEST);
                    pnlColorEntitySpeed.add(new ColorPickerButton("Entity speed",
                            prefs.getInt("editor-color-entityspeed", Color.ORANGE.getRGB())) {
                        @Override
                        public void colorUpdated(Color color) {
                            prefs.putInt("editor-color-entityspeed", color.getRGB());
                        }
                    },  BorderLayout.EAST);

                    JPanel pnlColorSelectionBorder = new JPanel(new BorderLayout());
                    pnlColors.add(pnlColorSelectionBorder);
                    pnlColorSelectionBorder.add(new JLabel("Selection border"), BorderLayout.WEST);
                    pnlColorSelectionBorder.add(new ColorPickerButton("Selection border",
                            prefs.getInt("editor-color-selectionborder", Color.BLACK.getRGB())) {
                        @Override
                        public void colorUpdated(Color color) {
                            prefs.putInt("editor-color-selectionborder", color.getRGB());
                        }
                    }, BorderLayout.EAST);

                    JPanel pnlColorSelectionInterior = new JPanel(new BorderLayout());
                    pnlColors.add(pnlColorSelectionInterior);
                    pnlColorSelectionInterior.add(new JLabel("Selection interior"), BorderLayout.WEST);
                    pnlColorSelectionInterior.add(new ColorPickerButton("Selection interior",
                            prefs.getInt("editor-color-selectioninterior", new Color(0, 0, 0, 64).getRGB())) {
                        @Override
                        public void colorUpdated(Color color) {
                            prefs.putInt("editor-color-selectioninterior", color.getRGB());
                        }
                    },  BorderLayout.EAST);

            JPanel pnlFolders = new JPanel();
            tabs.add("Folders", pnlFolders);

                JPanel pnlTilemapsFile = new JPanel(new BorderLayout());
    }

    private abstract class ColorPickerButton extends JButton {
        public ColorPickerButton(String name, int originalColor) {
            super();

            Color color = new Color(originalColor, true);

            setIcon(new ImageIcon("img/options/color.png"));
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, color, color));
            addActionListener(e -> {
                Color c = JColorChooser.showDialog(OptionsWindow.this, name, color);
                if (c != null) {
                    colorUpdated(c);

                    SwingUtilities.invokeLater(() -> {
                        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, c, c));
                    });
                }
            });
        }

        public abstract void colorUpdated(Color color);
    }

    public static void main(String[] args) {
        new OptionsWindow(new MainController());
    }
}
