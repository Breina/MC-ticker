package gui.bettergui.windows.world;

import gui.Util;
import gui.bettergui.DesktopPane;
import gui.bettergui.time.PlayState;
import gui.controllers.TimeController;
import gui.controllers.WorldController;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

// TODO hook this class up to TimeController
public class TimeWindow extends WorldWindow {
	private static final long serialVersionUID = -6830958137411873462L;

	private final static int BTNSIZE = 60;

	private TimeController timeController;

	private JButton btnStart, btnStepBack, btnStepForward, btnEnd;
	private JToggleButton btnRushBack, btnPlayBack, btnPause, btnPlayForward, btnRushForward;
	private JLabel lblStep;

	private boolean isPaused, isBack;

	public TimeWindow(WorldController worldController) {
		super(worldController, "Time", true);
		
		setFrameIcon(Util.getIcon("time/clock.png"));

		this.timeController = worldController.getTimeController();

		isPaused = true;
		isBack = false;

		setLocation(164, 52);

		buildGUI();
	}

	public void buildGUI() {

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
		lblStep = new JLabel("0");
		
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

		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaused(true);
				setBackEnabled(false);

				timeController.setPlaystate(PlayState.START);
			}
		});

		btnRushBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaused(false);

				timeController.setPlaystate(PlayState.RUSHBACK);
			}
		});

		btnPlayBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaused(false);

				timeController.setPlaystate(PlayState.PLAYBACK);
			}
		});

		btnStepBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				timeController.setPlaystate(PlayState.STEPBACK);
			}
		});

		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaused(btnPause.isSelected());

				timeController.setPlaystate(PlayState.PAUSED);
			}
		});

		btnStepForward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setBackEnabled(true);

				timeController.setPlaystate(PlayState.STEPFORWARD);
			}
		});

		btnPlayForward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaused(false);

				timeController.setPlaystate(PlayState.PLAYFORWARD);
			}
		});

		btnRushForward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaused(false);

				timeController.setPlaystate(PlayState.RUSHFORWARD);
			}
		});

		btnEnd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPaused(true);

				timeController.setPlaystate(PlayState.END);
			}
		});

		add(contentPanel);

		pack();

		setLayer(DesktopPane.PALETTE_LAYER);
	}

	public void setBackEnabled(boolean b) {
		btnStart.setEnabled(b);
		btnRushBack.setEnabled(b);
		btnPlayBack.setEnabled(b);

		if (isPaused)
			btnStepBack.setEnabled(b);

		isBack = b;
	}

	// Always enabled
	// public void setForwardEnabled(boolean b) {
	// if (endFound)
	// btnEnd.setEnabled(b);
	// btnRushForward.setEnabled(b);
	// btnPlayForward.setEnabled(b);
	//
	// if (isPaused)
	// btnStepForward.setEnabled(b);
	// }

	public void setPaused(boolean b) {
		if (isBack)
			btnStepBack.setEnabled(b);

		btnStepForward.setEnabled(b);
		btnPause.setSelected(b);

		isPaused = b;
	}

	public void setStep(int step) {
		lblStep.setText(String.valueOf(step));
	}
}
