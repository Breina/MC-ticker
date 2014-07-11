package gui.bettergui.blocks;

import java.util.ArrayList;
import java.util.List;

public class BlockCategory {
	
	private String name;
	private List<BlockLogic> blockLogics;
	
	public BlockCategory() {
		blockLogics = new ArrayList<>();
	}
	
	public BlockCategory(String name) {
		this();
		
		this.name = name;
	}
	
	public void addBlock(BlockLogic blockLogic) {
		blockLogics.add(blockLogic);
	}
	
	public String getName() {
		return name;
	}
	
	public List<BlockLogic> getBlocks() {
		return blockLogics;
	}
}
