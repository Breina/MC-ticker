package gui.main;

public class Cord3S {

	public short x, y, z;
	
	public Cord3S(short row, short layer, short col) {
		this.x = row;
		this.y = layer;
		this.z = col;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
