package gui.bettergui.windows.main;

import gui.controllers.MainController;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import logging.ILogger;
import logging.Log;

public class LogWindow extends MainWindow implements ILogger {
	
	private static final long serialVersionUID = 3272280523525631777L;

	private JTextArea textBox;
	
	private MainController controller;

	public LogWindow(MainController controller) {
		super(controller, "Log", true);
		
		this.controller = controller;

		textBox = new JTextArea();
		textBox.setEditable(false);
		
		DefaultCaret caret = (DefaultCaret) textBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		setPreferredSize(new Dimension(1008, 350));
		setSize(1008, 350);
		setLocation(0, 1000);
		add(new JScrollPane(textBox));
		
		setupLogger();
	}
	
	private void setupLogger() {
		
		print(Log.getMessages());
		
		Log.setLogger(this);
		Log.setBufferring(false);		
	}

	@Override
	public void print(String message) {
		
		textBox.append(message);
	}	
	
}
