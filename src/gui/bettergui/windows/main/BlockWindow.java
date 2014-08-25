package gui.bettergui.windows.main;

import gui.bettergui.DesktopPane;
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
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import logging.Log;

public class BlockWindow extends MainWindow {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private final static int BTNSIZE = 25;
	
	private MainController mainController;
	private BlockController blockController;
	private TileController tileController;
	
	private Block selectedBlock;

	public BlockWindow(MainController mainController) {
		super(mainController, "Blocks", true);
		
		try {
			setFrameIcon(new ImageIcon(ImageIO.read(new File("img/tools/block.png"))));
			
		} catch (IOException e) {
			Log.e("Failed to set icon for blocks window: " + e.getMessage());
		}
		
		this.mainController = mainController;
		this.blockController = mainController.getBlockController();
		this.tileController = mainController.getTileController();
		
		setLocation(0, 52);
		
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
						mainController.setBlock(new Block(bl.getId(), (byte) 0)); // TODO not 0 data plz
					}
				});
				
			}
			
			main.add(pnlCategory);
		}
		
		pack();
		
		setLayer(DesktopPane.PALETTE_LAYER);		
	}
}
