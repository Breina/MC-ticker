package sim.objects;

/**TODO remove entire class
 * Simple ChunkCord system to use as key in a HashSet.
 * Minecraft makes their own kind of LongHashmap, but our worlds won't nearly get as big so that should be redundant.
 */
public class ChunkCord {
	
	private int x, z;
	
	public ChunkCord(int x, int z) {
		
		this.x = x;
		this.z = z;
	}
	
	@Override
	public int hashCode() {
		
		// I'm not sure how optimal this is, but I've got it from StackOverflow
		// TODO When optimizing, check if there aren't too many overlaps
		return x * 31 + z;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof ChunkCord))
			return false;
		
		ChunkCord cord = (ChunkCord) obj;
		return (cord.x == this.x && cord.z == this.z);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + z + ")";
	}
}
