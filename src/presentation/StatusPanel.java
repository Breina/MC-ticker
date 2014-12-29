package presentation;

import presentation.controllers.WorldController;
import presentation.main.Cord3S;
import presentation.objects.Block;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 8458427057926796379L;

	private JLabel lblWorld, lblSelection, lblBlock;
	
	public StatusPanel() {
		super();
		
		setLayout(new GridLayout(1, 4));
		
		lblWorld = new JLabel();
		lblSelection = new JLabel();
		lblBlock = new JLabel();
		
		add(lblWorld);
		add(lblSelection);
		add(lblBlock);
		
		setPreferredSize(new Dimension(0, 20));
		setBackground(Color.LIGHT_GRAY);
	}
	
	public void updateSelection(WorldController source, Cord3S cord) {
		
		if (cord == null) {
			lblWorld.setText("");
			lblSelection.setText("");
			lblBlock.setText("");
			
		} else {
			lblWorld.setText(source.getWorldData().getName());
			lblSelection.setText(cord.toString());

			Block block = source.getWorldData().getBlock(cord.x, cord.y, cord.z);
			lblBlock.setText(block.getId() + " : " + block.getData());
		}
	}
}
