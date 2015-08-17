package presentation.gui.toolbar;

import presentation.controllers.MainController;
import presentation.controllers.WorldController;
import presentation.gui.editor.Editor;
import presentation.tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionListener;

public class Toolbar extends JToolBar {

    private final MainController mainController;
    private Tool currentTool;

    private JToggleButton[] buttons;

    public Toolbar(MainController mainController) {

        super("Toolbar");

        this.mainController = mainController;

        Tool[] tools = new Tool[]{new ToolActivate(mainController), new ToolSelect(mainController),
                new ToolPlace(mainController), new ToolRotate(mainController), new ToolUpdate(mainController),
                new ToolDebug(mainController)};

        buttons = new JToggleButton[tools.length];
        setLayout(new FlowLayout());
        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < tools.length; i++) {
            Tool tool = tools[i];
            addTool(group, tool, i);
        }
    }

    private void addTool(ButtonGroup group, final Tool tool, int index) {
        JToggleButton btn;

        if (tool.getFileName() == null)
            btn = new JToggleButton(tool.getName());
        else {
            btn = new JToggleButton(new ImageIcon("img/tools/" + tool.getFileName()));
            btn.setToolTipText(tool.getName());
        }

        btn.setPreferredSize(new Dimension(35, 35));
        add(btn);
        buttons[index] = btn;
        group.add(btn);

        if (group.getSelection() == null) {
            btn.setSelected(true);
            selectTool(tool);
        }

        btn.addActionListener(e -> selectTool(tool));
    }

    public void selectButton(int index) {
        if (index < 0 || index >= buttons.length)
            throw new IllegalArgumentException("Shortcut out of bounds");

        buttons[index].doClick();
    }

    private void selectTool(Tool tool) {
        for (WorldController worldController : mainController.getWorldControllers())
            for (Editor editor : worldController.getEditors()) {

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
