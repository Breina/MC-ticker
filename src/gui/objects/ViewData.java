package gui.objects;

import gui.exceptions.SchematicException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import logging.Log;
import sim.objects.WorldState;


/**
 * Contrains all data required for drawing
 */
public class ViewData {
	
	private short xSize, ySize, zSize;
	private String name;
	private Block[][][] blocks;
	
	private final static Block AIRBLOCK = new Block((byte) 0);
	
	public ViewData(String name, short width, short height, short length) {
		
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
	
	/**
	 * Loads the data required for drawing
	 * @param input The schematic
	 * @throws SchematicException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	// TODO remove method completely
//	public void setSchematic(InputStream input) throws SchematicException, IOException, NoSuchAlgorithmException {
//
//			setSchematic(Tag.readFrom(input));
//	}
//	
//	public void setSchematic(Tag input) throws SchematicException, IOException {
//
////		if (schematic != null) {
////			schematic.replaceTags(input);
////						
////		} else
//			schematic = input;
//		
//		if (Constants.DEBUG_TAG_SCHEMATICS) {
//			System.out.println("Loading tag schematic:");
//			schematic.print();
//		}
//		
//		if (!input.findNextTagByName("Materials", null).getValue().equals("Alpha"))
//			throw new SchematicException("The schematic is encoded for Minecraft classic, which is not supported.");
//
//		xSize = (short) input.findTagByName("Width").getValue();
//		ySize = (short) input.findTagByName("Height").getValue();
//		zSize = (short) input.findTagByName("Length").getValue();
//
//		setState((byte[]) input.findTagByName("Blocks").getValue(), (byte[]) input.findTagByName("Data").getValue());
//	}
	
	public void setState(WorldState state) throws SchematicException {
		
		long size = xSize * ySize * zSize;
		
		byte[] ids = state.getIds();
		byte[] data = state.getData();
		
		if (size != ids.length || size != data.length)
			throw new SchematicException("The state does not match the dimensions specified.");
		
		blocks = new Block[xSize][ySize][zSize];

		int i = 0;
		for (short y = 0; y < ySize; y++)
			for (short z = 0; z < zSize; z++)
				for (short x = 0; x < xSize; x++) {
					blocks[x][y][z] = new Block(ids[i], data[i]);
					i++;
				}
	}
	
	/**
	 * Reloads edited data into the schematic
	 * @param output Where to write it to
	 * @throws SchematicException
	 * @throws IOException
	 */
//	public void getSchematic(OutputStream output) throws SchematicException, IOException {
//		
//		if (!upToDate) {
//		
////			schematic.findTagByName("Width").setValue(xSize);
////			schematic.findTagByName("Height").setValue(ySize);
////			schematic.findTagByName("Length").setValue(zSize);
//				
//			int length = xSize * ySize * zSize;
//			byte[] aBlocks = new byte[length];
//			byte[] aData = new byte[length];
//			
//			int i = 0;
//			Block b;
//			
//			for (short y = 0; y < ySize; y++)
//				for (short z = 0; z < zSize; z++)
//					for (short x = 0; x < xSize; x++) {
//						b = blocks[x][y][z];
//						aBlocks[i] = b.getId();
//						aData[i] = b.getData();
//						i++;
//					}
//			
//			schematic.findTagByName("Block").setValue(aBlocks);
//			schematic.findTagByName("Data").setValue(aData);
//			
//			if (Constants.DEBUG_TAG_SCHEMATICS) {
//				System.out.println("Saving tag schematic:");
//				schematic.print();
//			}
//		}
//			
//		schematic.writeTo(output);
//	}
	
	public void setBlock(int x, int y, int z, Block block) {
		
		blocks[x][y][z] = block;
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

	public Block[][][] getBlocks() {
		return blocks;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
