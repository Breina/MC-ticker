package presentation.gui.toolbar;

import presentation.Util;
import presentation.controllers.MainController;
import presentation.controllers.TimeController;
import presentation.controllers.WorldController;
import presentation.gui.WorldListener;
import presentation.gui.time.PlayState;
import presentation.gui.time.TimeInfo;

import javax.swing.*;
import java.awt.*;

public class Timebar extends JToolBar implements WorldListener, TimeInfo {

    private final static int BTNSIZE = 60;

    private final MainController controller;
    private JComboBox<WorldController> worldChooser;

    private TimeController timeController;

    private JButton btnStart, btnStepBack, btnStepForward, btnEnd;
    private JToggleButton btnRushBack, btnPlayBack, btnPause, btnPlayForward, btnRushForward;
    private JLabel lblStep;

    private boolean isPaused, isBack, locked;

    public Timebar(MainController mainController) {
        super("Timebar");

        this.controller = mainController;

        controller.addWorldListener(this);

        isPaused = true;
        isBack = false;

        buildGUI();
    }

    void buildGUI() {

        worldChooser = new JComboBox<>();
        worldChooser.setPreferredSize(new Dimension(150, 35));
        worldChooser.setModel(new DefaultComboBoxModel(controller.getWorldControllers().toArray()));
        add(worldChooser);

        JPanel contentPanel = new JPanel(new GridLayout(1, 9));
        contentPanel.setSize(BTNSIZE * 9, BTNSIZE);

        ButtonGroup group = new ButtonGroup();

        btnStart = new JButton(Util.getIcon("time/start.png"));
        btnRushBack = new JToggleButton(Util.getIcon("time/rush-back.png"));
        btnPlayBack = new JToggleButton(Util.getIcon("time/play-back.png"));
        btnStepBack = new JButton(Util.getIcon("time/step-back.png"));
        btnPause = new JToggleButton(Util.getIcon("time/pause.png"));
        btnStepForward = new JButton(Util.getIcon("time/step-forward.png"));
        btnPlayForward = new JToggleButton(Util.getIcon("time/play-forward.png"));
        btnRushForward = new JToggleButton(Util.getIcon("time/rush-forward.png"));
        btnEnd = new JButton(Util.getIcon("time/end.png"));

        lblStep = new JLabel("0", SwingConstants.CENTER);
        lblStep.setFont(new Font(null, Font.PLAIN, 18));

        btnStart.setToolTipText("Jump to earliest state");
        btnRushBack.setToolTipText("Play backwards fast");
        btnPlayBack.setToolTipText("Play backwards at regular speed");
        btnStepBack.setToolTipText("Go back one tick");
        btnPause.setToolTipText("The pause icon should be pretty universal");
        btnStepForward.setToolTipText("Do one tick");
        btnPlayForward.setToolTipText("Minecraft default");
        btnRushForward.setToolTipText("Minecraft on steroids");
        btnEnd.setToolTipText("Go to last performed state");

        group.add(btnRushBack);
        group.add(btnPlayBack);
        group.add(btnPause);
        group.add(btnPlayForward);
        group.add(btnRushForward);

        setPaused(true);
        setBackEnabled(false);

        contentPanel.add(btnStart);
        contentPanel.add(btnRushBack);
        contentPanel.add(btnPlayBack);
        contentPanel.add(btnStepBack);
        contentPanel.add(btnPause);
        contentPanel.add(btnStepForward);
        contentPanel.add(btnPlayForward);
        contentPanel.add(btnRushForward);
        contentPanel.add(btnEnd);
        contentPanel.add(lblStep);

        worldChooser.addActionListener(ae -> {
            WorldController worldController = (WorldController) worldChooser.getSelectedItem();

            if (worldController == null) {
                setPaused(true);
                setEnabled(false);
                lblStep.setText("");

            } else {
                setEnabled(true);
                timeController = worldController.getTimeController();
                setStep(timeController.getTickCount());
            }
        });
        btnStart.addActionListener(ae -> {
            setPaused(true);
            setBackEnabled(false);

            timeController.setPlaystate(PlayState.START);
        });

        btnRushBack.addActionListener(ae -> {
            setPaused(false);

            timeController.setPlaystate(PlayState.RUSHBACK);
        });
        btnPlayBack.addActionListener(ae -> {
            setPaused(false);
            timeController.setPlaystate(PlayState.PLAYBACK);
        });
        btnStepBack.addActionListener(ae -> timeController.setPlaystate(PlayState.STEPBACK));
        btnPause.addActionListener(ae -> {
            setPaused(btnPause.isSelected());

            timeController.setPlaystate(PlayState.PAUSED);
        });

        btnStepForward.addActionListener(ae -> {
            setBackEnabled(true);

            timeController.setPlaystate(PlayState.STEPFORWARD);
        });

        btnPlayForward.addActionListener(ae -> {
            setPaused(false);
            timeController.setPlaystate(PlayState.PLAYFORWARD);
        });
        btnRushForward.addActionListener(ae -> {
            setPaused(false);
            timeController.setPlaystate(PlayState.RUSHFORWARD);
        });
        btnEnd.addActionListener(ae -> {
            setPaused(true);

            timeController.setPlaystate(PlayState.END);
        });
        add(contentPanel);
    }

    public void setEnabled(boolean active) {

        if (locked)
            return;

        btnStart.setEnabled(active);
        btnRushBack.setEnabled(active);
        btnPlayBack.setEnabled(active);
        btnStepBack.setEnabled(active);
        btnPause.setEnabled(active);
        btnStepForward.setEnabled(active);
        btnPlayForward.setEnabled(active);
        btnRushForward.setEnabled(active);
        btnEnd.setEnabled(active);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setBackEnabled(boolean b) {

        if (locked)
            return;

        btnStart.setEnabled(b);
        btnRushBack.setEnabled(b);
        btnPlayBack.setEnabled(b);

        if (isPaused)
            btnStepBack.setEnabled(b);

        isBack = b;
    }

    @Override
    public void setPaused(boolean b) {

        if (locked)
            return;

        if (isBack)
            btnStepBack.setEnabled(b);

        btnStepForward.setEnabled(b);
        btnPause.setSelected(b);

        isPaused = b;
    }

    @Override
    public void setStep(int step) {
        lblStep.setText(String.valueOf(step));
    }

    @Override
    public void onWorldAdded(WorldController worldController) {
        worldChooser.addItem(worldController);
    }

    @Override
    public void onWorldRemoved(WorldController worldController) {
        worldChooser.removeItem(worldController);
    }
}
