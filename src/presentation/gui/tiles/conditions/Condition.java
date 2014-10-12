package presentation.gui.tiles.conditions;

import java.util.ArrayList;
import java.util.List;

import presentation.gui.tiles.Graphic;
import presentation.gui.tiles.Mirror;
import presentation.objects.Orientation;

public class Condition {

	private String name;
	private int rotation;
	private Mirror mirror;
	private List<Condition> conditions;
	
	public Condition() {
		this(new String());
	}
	
	public Condition(String name) {
		this.name	= name;
		rotation	= 0;
		mirror		= Mirror.NONE;
		conditions	= new ArrayList<>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	
	public void setMirror(Mirror mirror) {
		this.mirror = mirror;
	}
	
	public void addCondition(Condition condition) {
		conditions.add(condition);
	}
	
	protected void addToGraphic(Graphic g) {
		if (!name.isEmpty())
			g.addName(name);
		
		if (rotation != 0)
			g.setRotation(rotation);
		
		if (mirror != Mirror.NONE)
			g.setMirror(mirror);
	}
	
	public boolean eval(Graphic g, byte id, byte data, UsedMask usedMask, Orientation orientation, byte custom) {
		
		addToGraphic(g);

		for (Condition condition : conditions) {
			condition.eval(g, id, data, usedMask, orientation, custom);
		}
		
		return true;
	}
}
