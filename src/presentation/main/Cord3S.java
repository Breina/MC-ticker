package presentation.main;

public class Cord3S {

	public final short x;
    public final short y;
    public final short z;
	
	public Cord3S(short x, short y, short z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Cord3S(int x, int y, int z) {
		this((short) x, (short) y, (short) z);
	}
	
	public Cord3S add(Cord3S c) {
		return new Cord3S(x + c.x, y + c.y, z + c.z);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
