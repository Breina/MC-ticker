package presentation.objects;

/**
 * Contains all data required for drawing
 */
public class ViewData {
	
	private short xSize, ySize, zSize;
	private String name;
	private Block[][][] blocks;
	private Entity[] entities;
	
	private final static Block AIRBLOCK = new Block((byte) 0);
	
	public ViewData(String name, short width, short height, short length) {

		this.name = name;
		this.xSize = width;
		this.ySize = height;
		this.zSize = length;
	}

	public void setState(Block[][][] blocks, Entity[] entities) {

		setBlocks(blocks);
		setEntities(entities);
	}

	public void setBlocks(Block[][][] blocks) {
		this.blocks = blocks;
	}

	public void setEntities(Entity[] entities) {
		this.entities = entities;
	}
	
	public Block getBlock(int x, int y, int z) {
		
		if (isOutbounds(x, y, z))
			return AIRBLOCK;
		
		return blocks[x][y][z];
	}
	
	public boolean isOutbounds(int x, int y, int z) {
		
		return (x < 0 || x >= xSize ||
				y < 0 || y >= ySize ||
				z < 0 || z >= zSize);
	}

	/**
	 * Width
	 */
	public short getXSize() {
		return xSize;
	}

	/**
	 * Height
	 */
	public short getYSize() {
		return ySize;
	}

	/**
	 * Length
	 */
	public short getZSize() {
		return zSize;
	}

	public String getName() {
		return name;
	}

	public Entity[] getEntities() {
		return entities;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
