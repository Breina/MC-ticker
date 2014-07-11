package gui.bettergui.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BlockSet {

	private String name;
	private List<BlockCategory> cats;
	HashMap<Byte, BlockLogic> blockLogics;
	
	public BlockSet() {
		cats = new ArrayList<>();
		blockLogics = new HashMap<>();
	}
	
	public BlockSet(String name) {
		this();
		
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<BlockCategory> getCategories() {
		return cats; 
	}
	
	public void addCategory(BlockCategory cat) {
		cats.add(cat);
		
		Iterator<BlockLogic> i = cat.getBlocks().iterator();
		
		while (i.hasNext()) {
			
			BlockLogic b = i.next();
			blockLogics.put(b.getId(), b);
		}
	}
	
	public boolean containsBlock(byte id) {
		return blockLogics.containsKey(id);
	}
	
	public BlockLogic getBlock(byte id) {
		return blockLogics.get(id);
	}
}
