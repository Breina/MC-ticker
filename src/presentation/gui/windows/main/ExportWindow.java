package presentation.gui.windows.main;

import logging.Log;
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
import presentation.threads.ExportSeriesRunnable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ExportWindow extends InternalWindow implements WorldListener, Runnable {

    private final JComboBox<WorldController> worldChooser;
    private final JTextField filePath;
    private final JButton btnOK;
    private final JRadioButton rbTop, rbFront, rbRight;
    private final JSpinner spSingleTime, spSingleLayer, spScale, spMinLayer, spMaxLayer, spConstantLayer, spMinTime,
        spMaxTime, spConstantTime, spGifDelay;
    private final SpinnerNumberModel singleTimeModel, singleLayerModel,
            minTimeModel, maxTimeModel, minLayerModel, maxLayerModel;
    private final JPanel pnlPreview;
    private final JTabbedPane pnlType;

    private Editor editor;

    private Orientation orientation;
    private int gifDelay, min, max, animationIndex;
    private boolean seriesTypeIsGif, seriesIsSlices, isPaused, threadIsGo;

    private MainController mainController;

    public ExportWindow(JDesktopPane parent, MainController mainController) {
        super(parent, "Export", true);

        this.mainController = mainController;
        mainController.addWorldListener(this);

        isPaused = true;
        seriesTypeIsGif = true;
        gifDelay = 500;

        lockTime();

        // These will be set properly later
        singleTimeModel = new SpinnerNumberModel();
        minTimeModel = new SpinnerNumberModel();
        maxTimeModel = new SpinnerNumberModel();

        singleLayerModel = new SpinnerNumberModel(0, 0, 0, 1);
        minLayerModel = new SpinnerNumberModel(0, 0, 0, 1);
        maxLayerModel = new SpinnerNumberModel(1, 1, 1, 1);

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
                ButtonGroup orientationGroup = new ButtonGroup();
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

        pnlType = new JTabbedPane();

            JPanel pnlSingle = new JPanel();
                pnlSingle.setLayout(new BoxLayout(pnlSingle, BoxLayout.Y_AXIS));
                pnlSingle.setBorder(new EmptyBorder(5, 5, 5, 5));

                JPanel pnlSpaceTime = new JPanel(new GridLayout(1, 4, 10, 10));
                    pnlSpaceTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                    pnlSpaceTime.add(new JLabel("Layer"));
                    pnlSpaceTime.add(spSingleLayer = new JSpinner(singleLayerModel));
                    pnlSpaceTime.add(new JLabel("Time"));
                    pnlSpaceTime.add(spSingleTime = new JSpinner(singleTimeModel));

                pnlSingle.add(pnlSpaceTime);

            JPanel pnlSeries = new JPanel();
                pnlSeries.setLayout(new BoxLayout(pnlSeries, BoxLayout.Y_AXIS));
                pnlSeries.setBorder(new EmptyBorder(5, 5, 5, 5));

                JPanel pnlSeriesType = new JPanel(new BorderLayout(10, 10));
                    pnlSeriesType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                    JLabel lblType = new JLabel("Type");
                    lblType.setPreferredSize(new Dimension(100, 25));
                    pnlSeriesType.add(lblType, BorderLayout.WEST);

                    JPanel pnlSeriesTypeRadioButtons = new JPanel(new GridLayout(1, 2));
                        JRadioButton rbGif = new JRadioButton("Gif animation", true);
                        JRadioButton rbImages = new JRadioButton("Separate images");
                        pnlSeriesTypeRadioButtons.add(rbGif);
                        pnlSeriesTypeRadioButtons.add(rbImages);
                        ButtonGroup seriesType = new ButtonGroup();
                        seriesType.add(rbGif);
                        seriesType.add(rbImages);
                        pnlSeriesType.add(pnlSeriesTypeRadioButtons, BorderLayout.CENTER);

                JPanel pnlGifDelay = new JPanel(new BorderLayout(10, 10));
                    pnlGifDelay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                    JLabel lblGifDelay = new JLabel("Gif delay (ms)");
                    lblGifDelay.setPreferredSize(new Dimension(100, 25));
                    pnlGifDelay.add(lblGifDelay, BorderLayout.WEST);
                    spGifDelay = new JSpinner(new SpinnerNumberModel(gifDelay, 10, 10000 ,10));
                    pnlGifDelay.add(spGifDelay, BorderLayout.CENTER);

                JPanel pnlSlicesOrTime = new JPanel(new BorderLayout(10, 10));
                    pnlSlicesOrTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                    JLabel lblAnimateOver = new JLabel("Animate");
                    lblAnimateOver.setPreferredSize(new Dimension(100, 25));
                    pnlSlicesOrTime.add(lblAnimateOver, BorderLayout.WEST);

                    JPanel pnlSlicesOrTimeRadioButtons = new JPanel(new GridLayout(1, 2));
                        JRadioButton rbSlices = new JRadioButton("Slices");
                        JRadioButton rbTime = new JRadioButton("Time", true);
                        pnlSlicesOrTimeRadioButtons.add(rbSlices);
                        pnlSlicesOrTimeRadioButtons.add(rbTime);
                        ButtonGroup animateOver = new ButtonGroup();
                        animateOver.add(rbSlices);
                        animateOver.add(rbTime);
                        pnlSlicesOrTime.add(pnlSlicesOrTimeRadioButtons, BorderLayout.CENTER);

                JPanel pnlSlices = new JPanel(new GridLayout(0, 4, 10, 10));
                    pnlSlices.setBorder(BorderFactory.createTitledBorder("Slices"));
                    pnlSlices.add(new JLabel("Min layer"));
                    spMinLayer = new JSpinner(minLayerModel);
                    pnlSlices.add(spMinLayer);
                    pnlSlices.add(new JLabel("Max layer"));
                    spMaxLayer = new JSpinner(maxLayerModel);
                    pnlSlices.add(spMaxLayer);
                    pnlSlices.add(new JLabel("Constant time"));
                    spConstantTime = new JSpinner(singleTimeModel);
                    pnlSlices.add(spConstantTime);

                JPanel pnlTime = new JPanel(new GridLayout(0, 4, 10, 10));
                    pnlTime.setBorder(BorderFactory.createTitledBorder("Time"));
                    pnlTime.add(new JLabel("Min time"));
                    spMinTime = new JSpinner(minTimeModel);
                    pnlTime.add(spMinTime);
                    pnlTime.add(new JLabel("Max time"));
                    spMaxTime = new JSpinner(maxTimeModel);
                    pnlTime.add(spMaxTime);
                    pnlTime.add(new JLabel("Constant layer"));
                    spConstantLayer = new JSpinner(singleLayerModel);
                    pnlTime.add(spConstantLayer);

                pnlSeries.add(pnlSeriesType);
                pnlSeries.add(Box.createVerticalStrut(5));
                pnlSeries.add(pnlGifDelay);
                pnlSeries.add(Box.createVerticalStrut(5));
                pnlSeries.add(pnlSlicesOrTime);
                pnlSeries.add(Box.createVerticalStrut(5));
                pnlSeries.add(pnlSlices);
                pnlSeries.add(Box.createVerticalStrut(5));
                pnlSeries.add(pnlTime);

        pnlType.add("Single", pnlSingle);
            pnlType.add("Series", pnlSeries);

        btnOK = new JButton("Export");
        btnOK.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        pnlPreview = new JPanel();
        // Will also create editor
        setOrientation(Orientation.TOP);
        setSeriesIsSlices(false);

        // Once all components are there, add listeners, which require editor

        // Update the orientation accordingly
        rbTop.addActionListener(e -> setOrientation(Orientation.TOP));
        rbFront.addActionListener(e -> setOrientation(Orientation.FRONT));
        rbRight.addActionListener(e -> setOrientation(Orientation.RIGHT));

        // Change the layer
        spSingleLayer.addChangeListener(e -> {
            int value = (Integer) singleLayerModel.getValue();
            editor.setLayerHeight((short) value);
            editor.repaint();
        });

        // Change the time
        spSingleTime.addChangeListener(e -> {
            getWorld().getTimeController().gotoTickCount((Integer) singleTimeModel.getValue());
            getWorld().getEntityManager().updateEntities();
            editor.onSchematicUpdated();
        });

        // Change the scale
        spScale.addChangeListener(e -> {
            double value = (double) spScale.getValue();
            editor.setScale((float) value);
            editor.repaint();
            pack();
        });

        // Change the gif's speed
        spGifDelay.addChangeListener(e -> gifDelay = (Integer) spGifDelay.getValue());

        // Gif vs images
        rbGif.addActionListener(e -> {
            spGifDelay.setEnabled(true);
            seriesTypeIsGif = true;
        });

        rbImages.addActionListener(e -> {
            spGifDelay.setEnabled(false);
            seriesTypeIsGif = false;
        });

        // Slices vs time
        rbSlices.addActionListener(e -> setSeriesIsSlices(true));
        rbTime.addActionListener(e -> setSeriesIsSlices(false));

        // Spinner ranges
        spMinLayer.addChangeListener(e -> {
            maxLayerModel.setMinimum((Integer) minLayerModel.getValue() + 1);
            min = (int) minLayerModel.getValue();
            if (animationIndex < min)
                animationIndex = min;
        });
        spMaxLayer.addChangeListener(e -> {
            minLayerModel.setMaximum((Integer) maxLayerModel.getValue() - 1);
            max = (int) maxLayerModel.getValue();
            if (animationIndex > max)
                animationIndex = max;
        });
        spMinTime.addChangeListener(e -> {
            maxTimeModel.setMinimum((Integer) minTimeModel.getValue() + 1);
            min = (int) minTimeModel.getValue();
            if (animationIndex < min)
                animationIndex = min;
        });
        spMaxTime.addChangeListener(e -> {
            minTimeModel.setMaximum((Integer) maxTimeModel.getValue() - 1);
            max = (int) maxTimeModel.getValue();
            if (animationIndex > max)
                animationIndex = max;
        });

        pnlType.addChangeListener(e -> {
            if (pnlType.getSelectedIndex() == 0)
                isPaused = true;
            else {
                isPaused = false;
                synchronized (ExportWindow.this) {
                    notify();
                }
            }
        });

        // EXPORT! :D
        btnOK.addActionListener(new ExportHandler());

        JPanel contentPanel = new JPanel();
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

        updateTimeModel();
        worldSelectionUpdated();

        pack();

        new Thread(this).start();
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

        updatePreview();
    }

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

        int min = timeController.getTickStartRange();
        int max = timeController.getTickEndRange();

        singleTimeModel.setMinimum(min);
        singleTimeModel.setValue(timeController.getTickCount());
        singleTimeModel.setMaximum(max);

        minTimeModel.setMinimum(min);
        minTimeModel.setValue(min);
        minTimeModel.setMaximum(max - 1);

        maxTimeModel.setMinimum(min + 1);
        maxTimeModel.setValue(max);
        maxTimeModel.setMaximum(max);
    }

    private void updatePreview() {
        if (editor != null) {
            /*pnlPreview.*/remove(editor);

            if (getWorld() != null)
                getWorld().getEntityManager().removeEditor(editor);
        }

        if (getWorld() == null)
            return;

        int layer = (int) singleLayerModel.getValue();
        double scale = (double) spScale.getValue();

        editor = new Editor(getWorld(), null, (short) layer, (float) scale, orientation);
        getWorld().getEntityManager().addEditor(editor);
        /*pnlPreview.*/add(editor, BorderLayout.CENTER);

        pack();
    }

    private void setSeriesIsSlices(boolean seriesIsSlices) {
        this.seriesIsSlices = seriesIsSlices;

        spMinTime.setEnabled(!seriesIsSlices);
        spMaxTime.setEnabled(!seriesIsSlices);
        spConstantLayer.setEnabled(!seriesIsSlices);

        spMinLayer.setEnabled(seriesIsSlices);
        spMaxLayer.setEnabled(seriesIsSlices);
        spConstantTime.setEnabled(seriesIsSlices);

        if (seriesIsSlices) {
            min = (int) minLayerModel.getValue();
            max = (int) maxLayerModel.getValue();
        } else {
            min = (int) minTimeModel.getValue();
            max = (int) maxTimeModel.getValue();
        }
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

        singleLayerModel.setMaximum(max);
        if (((int) singleLayerModel.getValue()) > max)
            singleLayerModel.setValue(max);

        minLayerModel.setMaximum(max - 1);
        if (((int) minLayerModel.getValue()) > max - 1)
            minLayerModel.setValue(max - 1);

        maxLayerModel.setMaximum(max);
        if (((int) maxLayerModel.getValue()) > max)
            maxLayerModel.setMaximum(max);

        this.orientation = orientation;
        updatePreview();

        if (seriesIsSlices) {
            this.min = (int) minLayerModel.getValue();
            this.max = (int) maxLayerModel.getValue();
        }
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

        threadIsGo = false;
        synchronized (ExportWindow.this) {
            notify();
        }

        super.dispose();
    }

    @Override
    public synchronized void run() {

        try {

            threadIsGo = true;

            while (threadIsGo) {

                if (isPaused)
                    wait();

                if (seriesIsSlices) {
                    editor.setLayerHeight((short) animationIndex);
                    singleLayerModel.setValue(animationIndex);
                } else {
                    getWorld().getTimeController().gotoTickCount(animationIndex);
                    singleTimeModel.setValue(animationIndex);
                }

                animationIndex++;

                if (animationIndex > max)
                    animationIndex = min;

                repaint();

                wait(gifDelay);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ExportHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (isPaused)
                    ImageIO.write(editor.getImage(), "png", new File(filePath.getText() + File.separator +
                            getWorld().getWorldData().getName() + ".png"));

                else {
                    ExportSeriesRunnable exportRunnable;

                    File outputFolder = new File(filePath.getText());
                    int constant;

                    if (seriesIsSlices)
                        constant = (int) singleTimeModel.getValue();
                    else
                        constant = (int) singleLayerModel.getValue();

                    if (seriesTypeIsGif)
                        exportRunnable = new ExportSeriesRunnable(outputFolder, getWorld(), editor, orientation,
                                seriesIsSlices, constant, min, max, gifDelay);
                    else
                        exportRunnable = new ExportSeriesRunnable(outputFolder, getWorld(), editor, orientation,
                                seriesIsSlices, constant, min, max);

                    new Thread(exportRunnable).start();
                }

            } catch (IOException e1) {
                Log.e("File error: " + e1.getMessage());
                e1.printStackTrace();
            }
        }
    }
}