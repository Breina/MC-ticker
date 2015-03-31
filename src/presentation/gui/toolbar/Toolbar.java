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

    public Toolbar(MainController mainController) {

        super("Toolbar");

        this.mainController = mainController;

        setLayout(new FlowLayout());
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
            btn = new JToggleButton(new ImageIcon("img/tools/" + tool.getFileName()));
            btn.setToolTipText(tool.getName());
        }

        btn.setPreferredSize(new Dimension(35, 35));
        add(btn);
        group.add(btn);

        if (group.getSelection() == null) {
            btn.setSelected(true);
            selectTool(tool);
        }

        btn.addActionListener(e -> selectTool(tool));
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
