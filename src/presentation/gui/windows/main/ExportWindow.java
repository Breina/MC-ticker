package presentation.gui.windows.main;

import presentation.controllers.MainController;
import presentation.controllers.TimeController;
import presentation.controllers.WorldController;
import presentation.gui.WorldListener;
import presentation.gui.editor.Editor;
import presentation.gui.time.PlayState;
import presentation.gui.toolbar.Timebar;
import presentation.gui.windows.InternalWindow;
import presentation.main.Constants;
import presentation.objects.Orientation;
import presentation.objects.ViewData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ExportWindow extends InternalWindow implements WorldListener {

    private final JComboBox<WorldController> worldChooser;
    private final JTextField filePath;
    private final JButton btnOK;
//    private final JSpinner gifSpeed, scale;
    private final ButtonGroup orientationGroup;
    private final JRadioButton rbTop, rbFront, rbRight;
    private final JSpinner spSingleTime, spSingleLayer, spScale;
    private final SpinnerNumberModel timeModel, layerModel;
    private final JPanel contentPanel, pnlPreview;

    private Editor editor;

    private Orientation orientation;

    private MainController mainController;

    public ExportWindow(JDesktopPane parent, MainController mainController) {
        super(parent, "Export", true);

        this.mainController = mainController;
        mainController.addWorldListener(this);

        lockTime();

        timeModel = new SpinnerNumberModel();
        layerModel = new SpinnerNumberModel(0, 0, 0, 1);

        setLayout(new BorderLayout());

        JPanel pnlExportPath = new JPanel(new BorderLayout(10, 10));
            pnlExportPath.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            JLabel lblExportPath = new JLabel("Export path");
            lblExportPath.setPreferredSize(new Dimension(100, 25));
            pnlExportPath.add(lblExportPath, BorderLayout.WEST);
            filePath = new JTextField(new File(Constants.EXPORTDIR).getAbsolutePath(), 20);
            pnlExportPath.add(filePath, BorderLayout.CENTER);
            JButton btnBrowse = new JButton("Browse...");
            btnBrowse.addActionListener(new BrowsePathHandler());
            pnlExportPath.add(btnBrowse, BorderLayout.EAST);

        JPanel pnlWorldChooser = new JPanel(new BorderLayout(10, 10));
            pnlWorldChooser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            JLabel lblWorldChooser = new JLabel("World");
            lblWorldChooser.setPreferredSize(new Dimension(100, 25));
            pnlWorldChooser.add(lblWorldChooser, BorderLayout.WEST);
            worldChooser = new JComboBox<>();
            worldChooser.addActionListener(new WorldSelectedHandler());
            worldChooser.setModel(new DefaultComboBoxModel(mainController.getWorldControllers().toArray()));
            pnlWorldChooser.add(worldChooser, BorderLayout.CENTER);

        JPanel pnlOrientationSelector = new JPanel(new BorderLayout(10, 10));
            pnlOrientationSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            JLabel lblOrientation = new JLabel("Orientation");
            lblOrientation.setPreferredSize(new Dimension(100, 25));
            pnlOrientationSelector.add(lblOrientation, BorderLayout.WEST);

            JPanel pnlOrientationOptions = new JPanel(new GridLayout(1, 3));
                pnlOrientationOptions.add(rbTop = new JRadioButton("Top-down", true));
                pnlOrientationOptions.add(rbFront = new JRadioButton("Front"));
                pnlOrientationOptions.add(rbRight = new JRadioButton("Right"));
                orientationGroup = new ButtonGroup();
                orientationGroup.add(rbTop);
                orientationGroup.add(rbFront);
                orientationGroup.add(rbRight);
                pnlOrientationSelector.add(pnlOrientationOptions, BorderLayout.CENTER);

        JPanel pnlScale = new JPanel(new BorderLayout(10, 10));
            pnlScale.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            JLabel lblScale = new JLabel("Scale");
            lblScale.setPreferredSize(new Dimension(100, 25));
            pnlScale.add(lblScale, BorderLayout.WEST);
            pnlScale.add(spScale = new JSpinner(new SpinnerNumberModel(2.0d, 0.1d, 10d, 0.1d)), BorderLayout.CENTER);

//        JPanel pnlType = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            pnlType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
//            JLabel lblType = new JLabel("Type");
//            lblType.setPreferredSize(new Dimension(100, 25));
//            pnlType.add(lblType);
//            pnlType.add(rbSingle = new JRadioButton("Single image", true));
//            pnlType.add(rbSeries = new JRadioButton("Image series"));
//            pnlType.add(rbGif = new JRadioButton("GIF"));
//            typeGroup = new ButtonGroup();
//            typeGroup.add(rbSingle);
//            typeGroup.add(rbSeries);
//            typeGroup.add(rbGif);

        JTabbedPane pnlType = new JTabbedPane();

            JPanel pnlSingle = new JPanel();
                pnlSingle.setLayout(new BoxLayout(pnlSingle, BoxLayout.Y_AXIS));
                pnlSingle.setBorder(new EmptyBorder(5, 5, 5, 5));

                JPanel pnlSpaceTime = new JPanel(new GridLayout(1, 4, 10, 10));
                    pnlSpaceTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                    pnlSpaceTime.add(new JLabel("Layer"));
                    pnlSpaceTime.add(spSingleLayer = new JSpinner(layerModel));
                    pnlSpaceTime.add(new JLabel("Time"));
                    pnlSpaceTime.add(spSingleTime = new JSpinner(timeModel));

            pnlSingle.add(pnlSpaceTime);

            JPanel pnlSeries = new JPanel();
                pnlSeries.add(new JLabel("series OMGOMGOMG"));

            pnlType.add("Single", pnlSingle);
            pnlType.add("Series", pnlSeries);

        btnOK = new JButton("Export");

        pnlPreview = new JPanel();
        // Will also create editor
        setOrientation(Orientation.TOP);

        // Once all components are there, add listeners, which require editor
        rbTop.addActionListener(e -> setOrientation(Orientation.TOP));
        rbFront.addActionListener(e -> setOrientation(Orientation.FRONT));
        rbRight.addActionListener(e -> setOrientation(Orientation.RIGHT));

        spSingleLayer.addChangeListener(e -> {
            int value = (Integer) spSingleLayer.getValue();
            editor.setLayerHeight((short) value);
            editor.repaint();
        });

        spSingleTime.addChangeListener(e -> {
            getWorld().getTimeController().gotoTickCount((Integer) spSingleTime.getValue());
            getWorld().getEntityManager().updateEntities();
            editor.onSchematicUpdated();
        });

        spScale.addChangeListener(e -> {
            double value = (double) spScale.getValue();
            editor.setScale((float) value);
            editor.repaint();
            revalidate();
            pack();
        });


        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setMinimumSize(new Dimension(300, 0));

        contentPanel.add(pnlExportPath);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(pnlWorldChooser);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(pnlOrientationSelector);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(pnlScale);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(pnlType);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(btnOK);

        add(contentPanel, BorderLayout.WEST);
        add(/*new JScrollPane(*/pnlPreview/*)*/, BorderLayout.CENTER);

        worldSelectionUpdated();

        pack();
    }

    @Override
    public void onWorldAdded(WorldController worldController) {
        worldChooser.addItem(worldController);
    }

    @Override
    public void onWorldRemoved(WorldController worldController) {
        worldChooser.removeItem(worldController);
    }

    private void worldSelectionUpdated() {
        boolean isValid = getWorld() != null;

        btnOK.setEnabled(isValid);
        spSingleTime.setEnabled(isValid);
        spSingleLayer.setEnabled(isValid);
        rbTop.setEnabled(isValid);
        rbFront.setEnabled(isValid);
        rbRight.setEnabled(isValid);

        if (isValid) {
            updateTimeModel();
        }
        updatePreview();
    }

//    private class GifSpinnerHandler implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            gifSpeed.setEnabled(topGif.isSelected() || rightGif.isSelected() || frontGif.isSelected());
//        }
//    }

    private class WorldSelectedHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            worldSelectionUpdated();
        }
    }

    private class BrowsePathHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser(filePath.getText());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                filePath.setText(file.getPath());
            }
        }
    }

    private WorldController getWorld() {
        return (WorldController) worldChooser.getSelectedItem();
    }

    private void updateTimeModel() {
        WorldController worldController = getWorld();

        TimeController timeController = worldController.getTimeController();

        timeModel.setMinimum(timeController.getTickStartRange());
        timeModel.setValue(timeController.getTickCount());
        timeModel.setMaximum(timeController.getTickEndRange());
    }

    private void updatePreview() {
        if (editor != null) {
            /*pnlPreview.*/remove(editor);

            if (getWorld() != null)
                getWorld().getEntityManager().removeEditor(editor);
        }

        if (getWorld() == null)
            return;

        int layer = (int) layerModel.getValue();
        double scale = (double) spScale.getValue();

        editor = new Editor(getWorld(), null, (short) layer, (float) scale, orientation);
        getWorld().getEntityManager().addEditor(editor);
        /*pnlPreview.*/add(editor, BorderLayout.CENTER);

        pack();
    }

    private void setOrientation(Orientation orientation) {

        int max = 0;
        ViewData worldData = getWorld().getWorldData();

        switch (orientation) {
            case TOP:
                max = worldData.getYSize() - 1;
                break;

            case FRONT:
                max = worldData.getZSize() - 1;
                break;

            case RIGHT:
                max = worldData.getXSize() - 1;
        }

        layerModel.setMaximum(max);
        if (((int) layerModel.getValue()) > max)
            layerModel.setValue(max);

        this.orientation = orientation;
        updatePreview();
    }

    private void lockTime() {

        Timebar timeBar = mainController.getFrame().getTimebar();

        timeBar.setEnabled(false);
        timeBar.setLocked(true);

        java.util.List<WorldController> worlds = mainController.getWorldControllers();

        for (WorldController world : worlds)
            world.getTimeController().setPlaystate(PlayState.PAUSED);
    }



    @Override
    public void dispose() {

        TimeController timeController = getWorld().getTimeController();
        timeController.gotoTickCount(timeController.getTickCount());

        Timebar timeBar = mainController.getFrame().getTimebar();

        timeBar.setLocked(false);
        timeBar.setEnabled(true);

        super.dispose();
    }
}
