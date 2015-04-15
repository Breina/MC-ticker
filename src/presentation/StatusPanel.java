package presentation;

import presentation.controllers.WorldController;
import presentation.main.Cord3S;
import presentation.objects.Block;
import presentation.objects.Entity;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 8458427057926796379L;

	private final JLabel lblWorld;
    private final JLabel lblSelection;
    private final JLabel lblValue;
	
	public StatusPanel() {
		super();
		
		setLayout(new GridLayout(1, 4));
		
		lblWorld = new JLabel();
		lblSelection = new JLabel();
		lblValue = new JLabel();
		
		add(lblWorld);
		add(lblSelection);
		add(lblValue);
		
		setPreferredSize(new Dimension(0, 20));
		setBackground(Color.LIGHT_GRAY);
	}
	
	public void updateSelection(WorldController source, Cord3S cord) {
		
		if (cord == null) {
			lblWorld.setText("");
			lblSelection.setText("");
			lblValue.setText("");
			
		} else {
			lblWorld.setText(source.getWorldData().getName());
			lblSelection.setText(cord.toString());

			char block = source.getWorldData().getBlock(cord.x, cord.y, cord.z);
			lblValue.setText(Block.getId(block) + " : " + Block.getData(block));
		}
	}

	public void updateSelectedEntity(WorldController source, Entity entity) {

		lblWorld.setText(source.getWorldData().getName());
		lblSelection.setText(entity.getPosString());
		lblValue.setText(entity.getId());
	}
}
