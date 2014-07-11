package gui.bettergui;

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
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import logging.Log;

// TODO hook this class up to TimeController
public class TimeWindow extends WorldMenuWindow {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private final static int BTNSIZE = 60;
	
	private WorldController controller;
	
	private static boolean isInitialized;
	private static ImageIcon start, rushBack, playBack, stepBack, pause, stepForward, playForward, rushForward, end;
	
	private JButton btnStart, btnStepBack, btnStepForward, btnEnd;
	private JToggleButton btnRushBack, btnPlayBack, btnPause, btnPlayForward, btnRushForward;
	
	private boolean isPaused, endFound;

	public TimeWindow(WorldController controller) {
		super(controller, "Time", true);
		
		this.controller = controller;
		isPaused = true;
		endFound = false;
		
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
		
		group.add(btnRushBack);
		group.add(btnPlayBack);
		group.add(btnPause);
		group.add(btnPlayForward);
		group.add(btnRushForward);
		
		setPaused(true);
		setBackEnabled(false);
		btnEnd.setEnabled(false);
		
		contentPanel.add(btnStart);
		contentPanel.add(btnRushBack);
		contentPanel.add(btnPlayBack);
		contentPanel.add(btnStepBack);
		contentPanel.add(btnPause);
		contentPanel.add(btnStepForward);
		contentPanel.add(btnPlayForward);
		contentPanel.add(btnRushForward);
		contentPanel.add(btnEnd);
		
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setPaused(true);
				setForwardEnabled(true);
				setBackEnabled(false);
			}
		});
		
		btnRushBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setPaused(false);
				setForwardEnabled(true);
			}
		});
		
		btnPlayBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setPaused(false);
				setForwardEnabled(true);
			}
		});
		
		btnStepBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setForwardEnabled(true);
			}
		});
		
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setPaused(btnPause.isSelected());
			}
		});
		
		btnStepForward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setBackEnabled(true);
				
				controller.tick();
			}
		});
		
		btnPlayForward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setPaused(false);
				setBackEnabled(true);
			}
		});
		
		btnRushForward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setPaused(false);
				setBackEnabled(true);
			}
		});
		
		btnEnd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setPaused(true);
				setBackEnabled(true);
				setForwardEnabled(false);
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
	}
	
	public void setForwardEnabled(boolean b) {
		if (endFound)
			btnEnd.setEnabled(b);
		btnRushForward.setEnabled(b);
		btnPlayForward.setEnabled(b);
		
		if (isPaused)
			btnStepForward.setEnabled(b);
	}
	
	public void setPaused(boolean b) {
		btnStepBack.setEnabled(b);
		btnStepForward.setEnabled(b);
		btnPause.setSelected(b);
		
		isPaused = b;
	}
	
	public void setEndFound(boolean b) {
		endFound = b;
	}
}
