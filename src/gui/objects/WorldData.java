package gui.objects;

import gui.exceptions.SchematicException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import logging.Log;
import sim.constants.Constants;
import utils.Tag;


/**
 * Contrains all data required for drawing
 */
public class WorldData {
	
	private short xSize, ySize, zSize;
	private String name;
	private Block[][][] blocks;
	
	private File schematicFile;
	
	private Tag schematic;
	private boolean upToDate;
	
	private final static Block AIRBLOCK = new Block((byte) 0);
	
	public WorldData(File schematicFile) throws SchematicException, IOException, NoSuchAlgorithmException {
		
		this.name = schematicFile.getName();
		this.name = this.name.substring(0, this.name.indexOf('.'));
		
		this.upToDate = false;
		this.schematicFile = schematicFile;
		
		loadSchematic(new FileInputStream(schematicFile));
	}
	
	public void save() {
		
		try {
			saveSchematic(new FileOutputStream(schematicFile));
		} catch (SchematicException | IOException e) {
			
			Log.e("Could not save schematic: " + e.getMessage());
		}
	}
	
	public void load() {
		
		try {
			loadSchematic(new FileInputStream(schematicFile));
		} catch (SchematicException | IOException | NoSuchAlgorithmException e) {

			Log.e("Could not load schematic: " + e.getMessage());
		}
	}
	
	/**
	 * Loads the data required for drawing
	 * @param input The schematic
	 * @throws SchematicException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	// TODO remove method completely
	public void loadSchematic(InputStream input) throws SchematicException, IOException, NoSuchAlgorithmException {

			loadSchematic(Tag.readFrom(input));
	}
	
	public void loadSchematic(Tag input) throws SchematicException, IOException {

		if (schematic != null) {
			schematic.replaceTags(input);
						
		} else
			schematic = input;
		
		if (Constants.DEBUG_TAG_SCHEMATICS) {
			System.out.println("Loading tag schematic:");
			schematic.print();
		}
		
		if (!input.findNextTagByName("Materials", null).getValue().equals("Alpha"))
			throw new SchematicException("The schematic is encoded for Minecraft classic, which is not supported.");

		xSize = (short) input.findTagByName("Width").getValue();
		ySize = (short) input.findTagByName("Height").getValue();
		zSize = (short) input.findTagByName("Length").getValue();

		final long size = xSize * ySize * zSize;

		if (size > Integer.MAX_VALUE)
			throw new SchematicException("The schematic is too big to handle. Width: " + xSize + ", Height: " + ySize + ","
					+ "Length: " + zSize);

		byte[] ids = (byte[]) input.findTagByName("Blocks").getValue();
		byte[] data = (byte[]) input.findTagByName("Data").getValue();

		if (size != ids.length || size != data.length)
			throw new SchematicException("The schematic data does not match the dimensions specified.");

		blocks = new Block[xSize][ySize][zSize];

		int i = 0;
		for (short y = 0; y < ySize; y++)
			for (short z = 0; z < zSize; z++)
				for (short x = 0; x < xSize; x++) {
					blocks[x][y][z] = new Block(ids[i], data[i]);
					i++;
				}
		
		upToDate = true;
	}
	
	/**
	 * Reloads edited data into the schematic
	 * @param output Where to write it to
	 * @throws SchematicException
	 * @throws IOException
	 */
	public void saveSchematic(OutputStream output) throws SchematicException, IOException {
		
		if (!upToDate) {
		
//			schematic.findTagByName("Width").setValue(xSize);
//			schematic.findTagByName("Height").setValue(ySize);
//			schematic.findTagByName("Length").setValue(zSize);
				
			int length = xSize * ySize * zSize;
			byte[] aBlocks = new byte[length];
			byte[] aData = new byte[length];
			
			int i = 0;
			Block b;
			
			for (short y = 0; y < ySize; y++)
				for (short z = 0; z < zSize; z++)
					for (short x = 0; x < xSize; x++) {
						b = blocks[x][y][z];
						aBlocks[i] = b.getId();
						aData[i] = b.getData();
						i++;
					}
			
			schematic.findTagByName("Block").setValue(aBlocks);
			schematic.findTagByName("Data").setValue(aData);
			
			if (Constants.DEBUG_TAG_SCHEMATICS) {
				System.out.println("Saving tag schematic:");
				schematic.print();
			}
		}
			
		schematic.writeTo(output);
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		
		blocks[x][y][z] = block;
		upToDate = false;
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
	
	public void setSchematicFile(File schematicFile) {
		this.schematicFile = schematicFile;
	}
	
	public File getSchematicFile() {
		return schematicFile;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
