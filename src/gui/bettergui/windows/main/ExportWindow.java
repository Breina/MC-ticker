package gui.bettergui.windows.main;

import gui.controllers.MainController;
import gui.controllers.WorldController;
import gui.main.Constants;
import gui.objects.WorldData;
import gui.threads.ExportRunnable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import logging.Log;

public class ExportWindow extends MainWindow {
	private static final long serialVersionUID = 4399955648841335487L;
	
	private MainController controller;
	
	private final JComboBox<WorldController> worldChooser;
	private final JCheckBox topLayers, topGif, rightLayers, rightGif, frontLayers, frontGif, publish;
	private final JButton btnOK, btnBrowse;
	private final JTextField filePath;
	private final JSpinner gifSpeed, scale;

	public ExportWindow(MainController controller) {
		super(controller, "Export", false);
		
		this.controller = controller;
		
		JPanel p0 = new JPanel();
			p0.setLayout(new BorderLayout());
			filePath = new JTextField(Constants.EXPORTDIR);
			p0.add(filePath, BorderLayout.CENTER);
			btnBrowse = new JButton("Browse...");
			btnBrowse.addActionListener(new BrowsePathHandler());
			p0.add(btnBrowse, BorderLayout.EAST);
		
		JPanel p1 = new JPanel();
			p1.setLayout(new BorderLayout());
			p1.add(new JLabel("World:"), BorderLayout.WEST);
			worldChooser = new JComboBox<>();
			worldChooser.addActionListener(new WorldSelectedHandler());
			worldChooser.setModel(new DefaultComboBoxModel(controller.getWorldControllers().toArray()));
			p1.add(worldChooser, BorderLayout.CENTER);
		
		JPanel p2 = new JPanel();
			p2.setBorder(BorderFactory.createTitledBorder("Top-down:"));
			p2.setLayout(new GridLayout(1, 2));
			topLayers = new JCheckBox("Layers", true);
			p2.add(topLayers);
			topGif = new JCheckBox("GIF", true);
			topGif.addActionListener(new GifSpinnerHandler());
			p2.add(topGif);
			
		JPanel p3 = new JPanel();
			p3.setBorder(BorderFactory.createTitledBorder("Right:"));
			p3.setLayout(new GridLayout(1, 2));
			rightLayers = new JCheckBox("Layers");
			p3.add(rightLayers);
			rightGif = new JCheckBox("GIF");
			rightGif.addActionListener(new GifSpinnerHandler());
			p3.add(rightGif);
			
		JPanel p4 = new JPanel();
			p4.setBorder(BorderFactory.createTitledBorder("Front:"));
			p4.setLayout(new GridLayout(1, 2));
			frontLayers = new JCheckBox("Layers");
			p4.add(frontLayers);
			frontGif = new JCheckBox("GIF");
			frontGif.addActionListener(new GifSpinnerHandler());
			p4.add(frontGif);
			
		JPanel p5 = new JPanel();
			p5.setLayout(new BorderLayout());
			p5.add(new JLabel("Delay (ms):"), BorderLayout.WEST);
			gifSpeed = new JSpinner();
			gifSpeed.setModel(new SpinnerNumberModel(300, 0, Integer.MAX_VALUE, 10));
			p5.add(gifSpeed, BorderLayout.CENTER);
			
		JPanel p6 = new JPanel();
			p6.setLayout(new BorderLayout());
			p6.add(new JLabel("Scale:"), BorderLayout.WEST);
			scale = new JSpinner();
			scale.setModel(new SpinnerNumberModel(1.0d, 0.1d, 100.0d, 0.1d));
			p6.add(scale, BorderLayout.CENTER);
			
		//JPanel p7 = new JPanel();
		publish = new JCheckBox("Publish");		

		btnOK = new JButton("Export");
		btnOK.addActionListener(new ExportHandler());
		
		revalidateWorldSelection();

		setLayout(new GridLayout(9, 1));
		add(p0);
		add(p1);
		add(p2);
		add(p3);
		add(p4);
		add(p5);
		add(p6);
		add(publish);
		add(btnOK);		
		
		//setSize(200, 300);
		setPreferredSize(new Dimension(200, 360));
		pack();
	}
	
	public void onWorldAdded(WorldController worldController) {
		worldChooser.addItem(worldController);
	}
	
	public void onWorldRemoved(WorldController worldController) {
		worldChooser.removeItem(worldController);
	}
	
	private void revalidateWorldSelection() {
		if (btnOK != null)
			btnOK.setEnabled(worldChooser.getSelectedItem() != null);
	}
	
	private class GifSpinnerHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			gifSpeed.setEnabled(topGif.isSelected() || rightGif.isSelected() || frontGif.isSelected());
		}
	} 
	
	private class WorldSelectedHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			revalidateWorldSelection();
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
	
	private void handleExport() {
		btnOK.setEnabled(false);
				
		File exportDir = new File(filePath.getText());
		if (!exportDir.exists() || !exportDir.isDirectory()) {
			
			Log.e("Directory not found");
			btnOK.setEnabled(true);
			
			return;
		}
		
		boolean[] opts = {topLayers.isSelected(), topGif.isSelected(),
				rightLayers.isSelected(), rightGif.isSelected(),
				frontLayers.isSelected(), frontGif.isSelected()};
		
		WorldController worldController = (WorldController) worldChooser.getSelectedItem();
		
		int mils = (int) gifSpeed.getValue();
		double scaleDub = (double) scale.getValue();
		float scaleNum = (float) scaleDub;
		
		boolean publishBool = publish.isSelected();
		
		ExportRunnable exportRunnable = new ExportRunnable(btnOK, opts, worldController, exportDir, mils, scaleNum, publishBool);
		Thread t = new Thread(exportRunnable);
		t.start();
	}
	
	private class ExportHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			handleExport();
		}
	}
}
