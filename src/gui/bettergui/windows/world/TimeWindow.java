package gui.bettergui.windows.world;

import gui.bettergui.DesktopPane;
import gui.bettergui.time.PlayState;
import gui.controllers.TimeController;
import gui.controllers.WorldController;
import gui.objects.Block;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import logging.Log;

// TODO hook this class up to TimeController
public class TimeWindow extends WorldWindow {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private final static int BTNSIZE = 60;
	
	private TimeController timeController;
	
	private static boolean isInitialized;
	private static ImageIcon start, rushBack, playBack, stepBack, pause, stepForward, playForward, rushForward, end;
	
	private JButton btnStart, btnStepBack, btnStepForward, btnEnd;
	private JToggleButton btnRushBack, btnPlayBack, btnPause, btnPlayForward, btnRushForward;
	private JLabel lblStep;
	
	private boolean isPaused, isBack;

	public TimeWindow(WorldController worldController) {
		super(worldController, "Time", true);
		
		this.timeController = worldController.getTimeController();
		
		isPaused = true;
		isBack = false;
		
		if (!isInitialized) {
			
			try {
				start = getIcon("start");
				rushBack = getIcon("rush-back");
				playBack = getIcon("play-back");
				stepBack = getIcon("step-back");
				pause = getIcon("pause");
				stepForward = getIcon("step-forward");
				playForward = getIcon("play-forward");
				rushForward = getIcon("rush-forward");
				end = getIcon("end");
				
				isInitialized = true;
			
			} catch (IOException e) {
				Log.e("Failed to load time-icons: " + e.getMessage());
			}
		}
		
		setLocation(152, 0);
		
		buildGUI();
	}
	
	private ImageIcon getIcon(String name) throws IOException {		
		return new ImageIcon(ImageIO.read(new File("img/time/" + name + ".png")));
	}
	
	public void buildGUI() {
		
		JPanel contentPanel = new JPanel(new GridLayout(1, 9));
		contentPanel.setSize(BTNSIZE * 9, BTNSIZE);
		
		ButtonGroup group = new ButtonGroup();
		
		btnStart = new JButton(start);
		btnRushBack = new JToggleButton(rushBack);
		btnPlayBack = new JToggleButton(playBack);
		btnStepBack = new JButton(stepBack);
		btnPause = new JToggleButton(pause);
		btnStepForward = new JButton(stepForward);
		btnPlayForward = new JToggleButton(playForward);
		btnRushForward = new JToggleButton(rushForward);
		btnEnd = new JButton(end);
		lblStep = new JLabel("0");
		
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
//	public void setForwardEnabled(boolean b) {
//		if (endFound)
//			btnEnd.setEnabled(b);
//		btnRushForward.setEnabled(b);
//		btnPlayForward.setEnabled(b);
//		
//		if (isPaused)
//			btnStepForward.setEnabled(b);
//	}
	
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
