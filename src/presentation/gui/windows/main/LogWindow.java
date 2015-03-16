package presentation.gui.windows.main;

import logging.ILogger;
import logging.Log;
import presentation.controllers.MainController;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class LogWindow extends MainWindow implements ILogger {
	
	private static final long serialVersionUID = 3272280523525631777L;

	private final JTextArea textBox;

	public LogWindow(JDesktopPane parent, MainController controller) {
		super(parent, controller, "Log", true);

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
