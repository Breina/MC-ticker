package presentation.gui;

import logging.ILogger;
import logging.Log;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class LogPane extends JSplitPane implements ILogger {

    private final JTextArea textBox;

    public LogPane(JComponent topComponent) {
        super(JSplitPane.VERTICAL_SPLIT, true);

        textBox = new JTextArea();
        textBox.setEditable(false);

        DefaultCaret caret = (DefaultCaret) textBox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setTopComponent(topComponent);
        setBottomComponent(new JScrollPane(textBox));

        setOneTouchExpandable(true);
        setResizeWeight(0.86);

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
