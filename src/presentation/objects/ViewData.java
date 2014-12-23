package presentation.objects;

import presentation.controllers.SimController;
import presentation.exceptions.SchematicException;
import sim.objects.WorldState;


/**
 * Contains all data required for drawing
 */
public class ViewData {
	
	private short xSize, ySize, zSize;
	private String name;
	private Block[][][] blocks;
	private Entity[] entities;

	private SimController controller;
	
	private final static Block AIRBLOCK = new Block((byte) 0);
	
	public ViewData(SimController controller, String name, short width, short height, short length) {

		this.controller = controller;

		this.name = name;
		
		this.xSize = width;
		this.ySize = height;
		this.zSize = length;
		
		blocks = new Block[xSize][ySize][zSize];
	}
	
	public void save() {
		
//		try {
			// TODO
//			getSchematic(new FileOutputStream(schematicFile));
//			
//		} catch (SchematicException | IOException e) {
//			
//			Log.e("Could not save schematic: " + e.getMessage());
//		}
	}
	
//	public void load() {
//		
//		try {
//			setSchematic(new FileInputStream(schematicFile));
//		} catch (SchematicException | IOException | NoSuchAlgorithmException e) {
//
//			Log.e("Could not load schematic: " + e.getMessage());
//		}
//	}
	
	public void setState(WorldState state) throws SchematicException {
		blocks = state.getBlocks();

		entities = controller.createViewDataFromEntities(state.getEntities().toArray());
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
