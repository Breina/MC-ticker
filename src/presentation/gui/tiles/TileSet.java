package presentation.gui.tiles;

import presentation.gui.tiles.conditions.Condition;
import presentation.gui.tiles.conditions.UsedMask;
import presentation.objects.Orientation;

public class TileSet {
	
	private final String name;
	private final String extension;
	
	private final Condition[] entries;
	
	public TileSet(String name, String extension) {
		this.name = name;
		this.extension = extension;
		
		entries = new Condition[256];
	}
	
	public void addEntry(short id, Condition entry) {
		entries[id] = entry;
	}
	
	public boolean containsId(short id) {

        return entries[id] != null;
	}

	public Graphic getGraphic(short id, byte data, Orientation orientation, byte custom) {
		
		Graphic graphic = new Graphic();
		entries[id].eval(graphic, (byte) id, data, new UsedMask(), orientation, custom);
		
		return graphic;
	}
}
