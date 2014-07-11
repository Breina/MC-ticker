package gui.bettergui.tiles;

import gui.bettergui.tiles.conditions.Condition;
import gui.bettergui.tiles.conditions.UsedMask;
import gui.objects.Orientation;

public class TileSet {
	
	private String name;
	private String extension;
	
	private Condition[] entries;
	
	public TileSet(String name, String extension) {
		this.name = name;
		this.extension = extension;
		
		entries = new Condition[256];
	}
	
	public void addEntry(short id, Condition entry) {
		entries[id] = entry;
	}
	
	public boolean containsId(short id) {
		
		if (entries[id] == null)
			return false;
		else
			return true;
	}

	public Graphic getGraphic(short id, byte data, Orientation orientation, byte custom) {
		
		Graphic graphic = new Graphic();
		entries[id].eval(graphic, (byte) id, data, new UsedMask(), orientation, custom);
		
		return graphic;
	}
}
