package presentation.gui.tools;

import presentation.Util;
import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.gui.editor.EditorPanel;
import presentation.gui.windows.world.DrawingWindow;
import presentation.tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;

public class Toolbar extends JToolBar {

    private MainController mainController;
    private Tool currentTool;

    public Toolbar(MainController mainController) {

        super("Toolbar");

        this.mainController = mainController;

        buildGUI();
    }

    public void buildGUI() {

        setLayout(new GridLayout(1, 5));

        ButtonGroup group = new ButtonGroup();
        addTool(group, new ToolActivate(mainController));
        addTool(group, new ToolSelect(mainController));
        addTool(group, new ToolPlace(mainController));
        addTool(group, new ToolRotate(mainController));
        addTool(group, new ToolDebug(mainController));
    }

    private void addTool(ButtonGroup group, final Tool tool) {
        JToggleButton btn;

        if (tool.getFileName() == null)
            btn = new JToggleButton(tool.getName());
        else {
            btn = new JToggleButton(Util.getIcon("tools/" + tool.getFileName()));
            btn.setToolTipText(tool.getName());
        }

        add(btn);
        group.add(btn);

        if (group.getSelection() == null) {
            btn.setSelected(true);
            selectTool(tool);
        }

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectTool(tool);
            }
        });
    }

    public void selectTool(Tool tool) {
        for (WorldController worldController : mainController.getWorldControllers())
            for (DrawingWindow window : worldController.getOpenWindows()) {
                EditorPanel editor = window.getEditor();

                editor.removeMouseListener(currentTool);
                if (currentTool.hasMouseMotionListener())
                    editor.removeMouseMotionListener((MouseMotionListener) currentTool);

                editor.addMouseListener(tool);
                if (tool.hasMouseMotionListener())
                    editor.addMouseMotionListener((MouseMotionListener) tool);
            }

        currentTool = tool;
        mainController.setTool(tool);
    }

}
