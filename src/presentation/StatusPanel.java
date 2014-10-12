package presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import presentation.main.Cord3S;

public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 8458427057926796379L;

	private JLabel lblWorld, lblSelection;
	
	public StatusPanel() {
		super();
		
		setLayout(new GridLayout(1, 4));
		
		lblWorld = new JLabel();
		lblSelection = new JLabel();
		
		add(lblWorld);
		add(lblSelection);
		
		setPreferredSize(new Dimension(0, 20));
		setBackground(Color.LIGHT_GRAY);
	}
	
	public void updateSelection(String worldName, Cord3S cords) {
		
		if (cords == null) {
			lblWorld.setText("");
			lblSelection.setText("");
			
		} else {
			lblWorld.setText(worldName);
			lblSelection.setText(cords.toString());
		}
	}
}
