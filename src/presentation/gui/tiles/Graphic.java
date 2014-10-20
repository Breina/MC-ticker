package presentation.gui.tiles;

import presentation.main.Constants;

public class Graphic {

	private StringBuilder sb;
	private int rotation;
	private Mirror mirror;
	
	public Graphic() {
		sb = new StringBuilder();
		rotation = 0;
		mirror = Mirror.NONE;
	}
	
	public void addName(String name) {
		
		if (sb.length() != 0)
			sb.append(Constants.GRAPHICSEPARATOR);
		
		sb.append(name);
	}
	
	public void setRotation(int rotation) {		
		this.rotation = rotation;
	}
	
	public void setMirror(Mirror mirror) {
		this.mirror = mirror;
	}
	
	public String getName() {
		return sb.toString();
	}
	
	public int getRotation() {
		return rotation;
	}
	
	public Mirror getMirror() {
		return mirror;
	}

	@Override
	public String toString() {
		return "Graphic [sb=" + sb + ", rotation=" + rotation + ", mirror=" + mirror + "]";
	}
	
	
}
