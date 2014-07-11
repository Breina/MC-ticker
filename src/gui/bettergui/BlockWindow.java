package gui.bettergui;

import gui.bettergui.blocks.BlockCategory;
import gui.bettergui.blocks.BlockLogic;
import gui.controllers.BlockController;
import gui.controllers.MainController;
import gui.controllers.TileController;
import gui.objects.Block;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class BlockWindow extends WindowMenuWindow {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private final static int BTNSIZE = 25;
	
//	private MainController mainController;
	private BlockController blockController;
	private TileController tileController;
	
	private Block selectedBlock;

	public BlockWindow(MainController mainController) {
		super(mainController, "Blocks", true);
		
//		this.mainController = mainController;
		this.blockController = mainController.getBlockController();
		this.tileController = mainController.getTileController();
		
		buildGUI();
	}
	
	public void buildGUI() {
		
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
				
				btn.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						selectedBlock = new Block(bl.getId(), (byte) 0);
					}
				});
				
			}
			
			main.add(pnlCategory);
		}
		
		pack();
		
		setLayer(DesktopPane.PALETTE_LAYER);		
	}
	
	
	
	public Block getSelectedBlock() {
		
		return selectedBlock;
	}
}
