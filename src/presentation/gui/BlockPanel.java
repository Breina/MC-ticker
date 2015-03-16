package presentation.gui;

import presentation.blocks.BlockCategory;
import presentation.blocks.BlockLogic;
import presentation.controllers.BlockController;
import presentation.controllers.MainController;
import presentation.controllers.TileController;
import presentation.objects.Block;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BlockPanel extends JPanel {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private final static int BTNSIZE = 25;
	
	private final MainController mainController;
	private final BlockController blockController;
	private final TileController tileController;

	public BlockPanel(MainController mainController) {
		super();
		
		this.mainController = mainController;
		this.blockController = mainController.getBlockController();
		this.tileController = mainController.getTileController();
		
		buildGUI();
	}
	
	private void buildGUI() {
		
		List<BlockCategory> categories = blockController.getAllCategories();
		ButtonGroup group = new ButtonGroup();
		
		JPanel main = new JPanel();
		add(main);
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		
		for (BlockCategory cat : categories) {
			
			List<BlockLogic> blocks = cat.getBlocks();
			int blockAmount = blocks.size();
			
			GridLayout gridLayout = new GridLayout((int) Math.ceil(blockAmount / 5.0), 5);
			JPanel pnlCategory = new JPanel(gridLayout);
			pnlCategory.setBorder(BorderFactory.createTitledBorder(cat.getName()));
			
			for (final BlockLogic bl : blocks) {
				
				if (bl.isHidden())
					continue;
				
				BufferedImage image = tileController.getTile(bl.getId(), bl.getIconData(), bl.getIconOrientation());
				
				JToggleButton btn;
				
				if (image == null)
					btn = new JToggleButton(bl.getName());
				else
					btn = new JToggleButton(new ImageIcon(image));
				
				btn.setPreferredSize(new Dimension(BTNSIZE, BTNSIZE));
				btn.setToolTipText(bl.getName());
				pnlCategory.add(btn);
				group.add(btn);

                if (group.getSelection() == null) {
                    btn.setSelected(true);
                    mainController.setBlock(new Block(bl.getId(), (byte) 0));
                }
				
				btn.addItemListener(e -> {
                    mainController.setBlock(new Block(bl.getId(), (byte) 0)); // TODO not 0 data plz
                });
			}
			
			main.add(pnlCategory);
		}
	}
}
