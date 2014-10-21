package sim.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import logging.Log;
import sim.constants.Constants;
import sim.exceptions.SchematicException;
import sim.objects.WorldInstance;
import sim.objects.WorldState;
import utils.Tag;
import utils.Tag.Type;

public class SimWorld {
	
	private RWorld rWorld;
	private RBlock rBlock;
	private RChunk rChunk;
	private RProfiler rProfiler;
	private RChunkProvider rChunkProvider;
	private RTileEntity rTileEntity;
	private RNBTTags rNBTTags;
	private REntity rEntity;
	private RNextTickListEntry rNextTickListEntry;
	private RChunkPrimer rChunkPrimer;
	
	private WorldInstance world;
	
	public SimWorld(RBlock rBlock, RChunk rChunk, RChunkProvider rChunkProvider, REntity rEntity,
			RNBTTags rNBTTags, RNextTickListEntry rNextTickListEntry, RProfiler rProfiler,
			RTileEntity rTileEntity, RWorld rWorld, RChunkPrimer rChunkPrimer) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		this.rBlock = rBlock;
		this.rChunk = rChunk;
		this.rChunkProvider = rChunkProvider;
		this.rEntity = rEntity;
		this.rNBTTags = rNBTTags;
		this.rNextTickListEntry = rNextTickListEntry;
		this.rProfiler = rProfiler;
		this.rTileEntity = rTileEntity;
		this.rWorld = rWorld;
		this.rChunkPrimer = rChunkPrimer;
	}
	
	public void createInstance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Log.i("Creating new world");
		world = rWorld.createInstance(Constants.WORLDTYPEID, Constants.WORLDTYPE, Constants.GAMETYPE,
				Constants.SEED, Constants.WORLDPROVIDER, Constants.MAPFEATURESENABLED, Constants.HARDCOREENABLED, rChunk, rChunkProvider,
				rProfiler);
	}
	
	/**
	 * 
	 * @param worldTypeId Between 0 and 15. Mc uses it like 0=default, 1=flat, 2=largeBiomes, 3=amplified, 8=default_1_1
	 * @param worldType The name of the type of world this is.
	 * @param gameType Should be either "NOT_SET", "SURVIVAL", "CREATIVE" or "ADVENTURE".
	 * @param seed The seed of the world.
	 * @param worldProvider -1=nether, 0=overworld, 1=end.
	 * @param hardcoreEnabled true/false
	 * @param difficulty 0=peaceful 1=easy 2=normal 3=hard
	 */
	public void createInstance(int worldTypeId, String worldType, String gameType, long seed, int worldProvider, boolean hardcoreEnabled, int difficulty) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		switch (worldTypeId) {
			case 1:
			case 2:
			case 3:
						Log.i("WorldType              id=" + worldTypeId + ", name=" + worldType);
				break;
				
			default:
						Log.w("Custom WordType:       id=" + worldTypeId + ", name=" + worldType);
		}
		
						Log.i("GameType               " + gameType);
						Log.i("Seed                   " + seed);
						
		switch (worldProvider) {
			case -1:
						Log.i("WorldProvider          Hell");
				break;
			case 0:
						Log.i("WorldProvider          Surface");
				break;
			case 1:
						Log.i("WorldProvider          End");
				break;
			default:
						Log.i("Custom WorldProvider   " + worldProvider);
		}
		
						Log.i("Hardcore               " + (hardcoreEnabled ? "Enabled" : "Disabled"));
						
		switch (difficulty) {
			case 0:
						Log.i("Difficulty             Peaceful");
				break;
			case 1:
						Log.i("Difficulty             Easy");
				break;
			case 2:
						Log.i("Difficulty             Normal");
				break;
			case 3:
						Log.i("Difficulty             Hard");
				break;
			default:
						Log.e("Custom Difficulty      " + difficulty);
		}		
		
		Log.i("Creating new world");
		world = rWorld.createInstance(worldTypeId, worldType, gameType, seed, worldProvider, Constants.MAPFEATURESENABLED, hardcoreEnabled, rChunk, rChunkProvider, rProfiler); 
	}
	
	public void createEmptyWorld(int xSize, int ySize, int zSize) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		int size = xSize * ySize * zSize;
		
		
		
		byte[] blockIds = new byte[size];
		byte[] blockData = new byte[size];
		
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				for (int z = 0; z < zSize; z++) {
					blockIds[size] = 0;
					blockData[size] = 0;
				}
			
		setWorldBlocks(xSize, ySize, zSize, blockIds, blockData);	
	}

	public void setWorldFromFile(File schematicFile) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchAlgorithmException {
		
		try {
			
			if (!schematicFile.exists())
				throw new SchematicException("File not found: " + schematicFile.getPath());

			if (!schematicFile.canRead())
				throw new SchematicException("Unauthorized to read file: " + schematicFile.getPath());
			
			setWorld(new FileInputStream(schematicFile));
			
		} catch (IOException | SchematicException e) {
			
			Log.e(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads in a schematic
	 * @param input A schematic-format inputstream (like FileInputStream from a .schematic)
	 * @return The id of the loaded world
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	public void setWorld(InputStream input) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchAlgorithmException {
		
		Tag schematicTag = Tag.readFrom(input);
		
		if (Constants.DEBUG_MC_SCHEMATICS) {
			System.out.println("SET");
			schematicTag.print();
		}
		
		setWorld(schematicTag);
	}
	
	public void setWorld(Tag schematicTag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ArrayIndexOutOfBoundsException, IOException {
		
		if (!schematicTag.getName().equals("Schematic"))
			Log.w("The root tag was not named 'Schematic', continuing anyway.");
		
		if (!schematicTag.findNextTagByName("Materials", null).getValue().equals("Alpha"))
			Log.w("The schematic is encoded for Minecraft classic or something else, which is not supported, but I'll try.");
		
		// Size
		world.setxSize((int) ((short) schematicTag.findNextTagByName("Width", null).getValue()));
		world.setySize((int) ((short) schematicTag.findNextTagByName("Height", null).getValue()));
		world.setzSize((int) ((short) schematicTag.findNextTagByName("Length", null).getValue()));
		
		// Blocks
		byte[] idsArray = (byte[]) schematicTag.findNextTagByName("Blocks", null).getValue();
		byte[] dataArray = (byte[]) schematicTag.findNextTagByName("Data", null).getValue();
		
		setWorldBlocks(world.getxSize(), world.getySize(), world.getzSize(), idsArray, dataArray);
		
		// TileEntities
		Tag tileEntities = schematicTag.findNextTagByName("TileEntities", null);
		rWorld.clearTileEntities(world);
		if (tileEntities != null)
			setWorldTileEntities((Tag[]) tileEntities.getValue());
		
		// Entities
		Tag entities = schematicTag.findNextTagByName("Entities", null);
		rWorld.clearEntities(world);
		// TODO
//		if (entities != null)
//			setWorldEntities(world, (Tag[]) entities.getValue());
		
		// TileTicks
		Tag tileTicks = schematicTag.findNextTagByName("TileTicks", null);
		rWorld.clearTickEntries(world);
		if (tileTicks != null)
			setWorldTileTicks((Tag[]) tileTicks.getValue());
	}
	
	/**
	 * Loads up a world from the SharedWorld object.
	 * 
	 * Assumes SharedWorld.blockIds has a constant width, height and length.
	 * Assumes SharedWorld.blockData has the exact same dimensions as SharedWorld.blockIds.
	 * @param world The world object to load in
	 */
	private void setWorldBlocks(int xSize, int ySize, int zSize, byte[] blockIds, byte[] blockDatas) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
		
		rChunkProvider.clear();
		
		int chunksX = (xSize - 1) / 16 + 1;
		int chunksZ = (zSize - 1) / 16 + 1;
		
		// A chunk's height needs to be a quadratic of 2
		int height = 1;
		while (ySize > height)
			height *= 2;
		
		int chunkSize = 256 * height;		
		
		for (int chunkX = 0; chunkX < chunksX; chunkX++)
			for (int chunkZ = 0; chunkZ < chunksZ; chunkZ++) {
				
				int worldXoffset = chunkX * 16;
				int worldZoffset = chunkZ * 16;
				
				
//				Object blocks = Array.newInstance(rBlock.getBlockClass(), chunkSize);
//				byte[] dataOLD = new byte[chunkSize];
				short[] data = new short[65536];
				
				if (Constants.DEBUG_SCHEMATIC_DATA) {
					System.out.println("(" + chunkX + ", " + chunkZ + ")");
					System.out.println("id\tx\ty\tz\tchIndex\tschIndex");
				}
				
				xLoop: for (int x = 0; x < 16; x++)
					zLoop: for (int z = 0; z < 16; z++)
						for (int y = 0; y < 256; y++) {
							
							int chunkIndex = x << 12 | z << 8 | y;
							int worldX = x + worldXoffset;
							int worldZ = z + worldZoffset;
							
							if (y >= ySize)
								break;
							
							if (worldZ >= zSize)
								break zLoop;
							
							if (worldX >= xSize)
								break xLoop;
							
							// Any OoB blocks will be left null and will be given the default block (air) by the game
								
							int schematicIndex = y * xSize * zSize + worldZ * xSize + worldX;
							
							data[chunkIndex] = (short) (((short) blockIds[schematicIndex]) << 4 |
											   blockDatas[schematicIndex]);
						}
				
				Object chunkPrimer = rChunkPrimer.createChunkPrimer();
				rChunkPrimer.setData(chunkPrimer, data);
				
				
				Object chunk = rChunk.createChunk(world.getWorld(), chunkPrimer, chunkX, chunkZ);
				
				rChunkProvider.addChunk(chunk, chunkX, chunkZ);
			}
	}

	/**
	 * Loads all tile entities from Tags, can run in a separate thread
	 * @param tags The tag array
	 */
	private void setWorldTileEntities(Tag[] tags) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		for (int i = 0; i < tags.length; i++) {
			
			Tag schematicTag = tags[i];
			
			int x = (int) schematicTag.findTagByName("x").getValue();
			int z = (int) schematicTag.findTagByName("z").getValue();
			
			Object mcTag = rNBTTags.getMinecraftTagFromTag(schematicTag);
			System.out.println(mcTag);
			
			Object chunk = rChunkProvider.getChunk(x, z);
			
			Object tileEntity = rTileEntity.createTileEntityFromNBT(mcTag);
			
			try {
//				rTileEntity.debug(tileEntity);
//				Object itemsTag = rNBTTags.getTagList(mcTag, "Items");
//				Object itemTag = rNBTTags.getCompoundTagAtObject(itemsTag, 0);
//				System.out.println("Loading item: " + itemTag);
//				Object itemStack = rTileEntity.readFromNBT(itemTag);
//				System.out.println("Loaded item:  " + itemStack);
//				Object itemStack = rTileEntity.loadItemStackFromNBT(itemTag);
//				System.out.println(item);
			} catch (Exception e) {
				System.out.println("No inventory");
				e.printStackTrace();
			}

			rChunk.addTileEntity(chunk, tileEntity);
			
			// TODO remove this line
//			rWorld.addTileEntity(world, tileEntity);
		}
	}
	
	private void setWorldEntities(Tag[] tags) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		for (int i = 0; i < tags.length; i++) {
			
			Tag schematicTag = tags[i];
			
			Object mcTag = rNBTTags.getMinecraftTagFromTag(schematicTag);
			
			Object entity = rEntity.createEntityFromNBT(mcTag, world.getWorld());
			
			rWorld.spawnEntityInWorld(world, entity);
		}
	}
	
	private void setWorldTileTicks(Tag[] tags) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		for (Tag tag : tags) {
			
			int xCoord		= (int) tag.findNextTagByName("x", null).getValue();
			int yCoord		= (int) tag.findNextTagByName("y", null).getValue();
			int zCoord		= (int) tag.findNextTagByName("z", null).getValue();
			
			int intBlock	= (int) tag.findNextTagByName("i", null).getValue();
			byte byteBlock	= (byte) intBlock;
			Object block	= rBlock.getBlockById(byteBlock);
			
			int time		= (int) tag.findNextTagByName("t", null).getValue();
			int priority	= (int) tag.findNextTagByName("p", null).getValue();
			
			rWorld.addTickEntry(world, xCoord, yCoord, zCoord, block, time, priority);
		}
	}
	
	public void getWorld(OutputStream os) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		byte[][] blocks = getWorldBlocks();
		
			Tag tWidth	= new Tag(Tag.Type.TAG_Short, "Width" , (short) world.getxSize());
			Tag tHeight = new Tag(Tag.Type.TAG_Short, "Height", (short) world.getySize());
			Tag tLength = new Tag(Tag.Type.TAG_Short, "Length", (short) world.getzSize());
			
			Tag tMaterials = new Tag(Tag.Type.TAG_String, "Materials", "Alpha");				
			Tag tBlocks = new Tag(Tag.Type.TAG_Byte_Array, "Blocks", blocks[0]);
			Tag tData = new Tag(Tag.Type.TAG_Byte_Array, "Data", blocks[1]);
			
			// Both of these can be null
			Tag tTileEntities = getWorldTileEntities();
			Tag tEntities = getWorldEntities();
			Tag tTileTicks = getWorldTileTicks();
			
			Tag tEnd = new Tag(Tag.Type.TAG_End, "", null);
			
		Tag tSchematic;
		
		tSchematic = new Tag(Tag.Type.TAG_Compound, "Schematic", new Tag[]{tHeight, tLength, tWidth, tMaterials, tData, tBlocks, tEnd});
		
		if (tTileEntities != null)
			tSchematic.addTag(tTileEntities);
		
		if (tEntities != null)
			tSchematic.addTag(tEntities);
		
		if (tTileTicks != null)
			tSchematic.addTag(tTileTicks);
		
		tSchematic.addTag(tEnd);
	
		if (Constants.DEBUG_MC_SCHEMATICS) {
			System.out.println("GET:");
			tSchematic.print();
		}
								
		tSchematic.writeTo(os);
		
		Log.i("Saving world");
	}
	
	private byte[][] getWorldBlocks() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		int size = world.getxSize() * world.getySize() * world.getzSize();
		
		byte[][] blocks = new byte[2][];
		blocks[0] = new byte[size];
		blocks[1] = new byte[size];
		
		int i = 0;
		
		if (Constants.DEBUG_SCHEMATIC_DATA)
			System.out.println("id\tx\ty\tz\tindex");
		
		for (int y = 0; y < world.getySize(); y++) {
			for (int z = 0; z < world.getzSize(); z++) {
				for (int x = 0; x < world.getxSize(); x++) {
					
					Object blockState = rWorld.getBlockState(world, x, y, z);
					Object block = rBlock.getBlockFromState(blockState);

					blocks[0][i] = (byte) rBlock.getIdFromBlock(block);
					blocks[1][i] = (byte) rBlock.getMetaFromState(block, blockState);
					
					if (Constants.DEBUG_SCHEMATIC_DATA)
						System.out.println(blocks[0][i] + "\t" + x + "\t" + y + "\t" + z + "\t" + i);
					
					i++;
				}
			}
		}
		
		
		return blocks;
	}
	
	private Tag getWorldTileEntities() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		List<Object> tileEntities = rWorld.getLoadedTileEntities(world);//world.getLoadedTileEntities();
		
		// TODO temp fix, fix this properly
		if (tileEntities.size() == 0)
			return null;
		
		Tag[] payload = new Tag[tileEntities.size()];
		Iterator<Object> i = tileEntities.iterator();
		
		System.out.println("GET");
		
		int j = 0;
		while (i.hasNext()) {
			
			Object mcTileEntity = i.next();
			Object mcTag = rNBTTags.newInstance();
			
			rTileEntity.getNBTFromTileEntity(mcTileEntity, mcTag);
			
			System.out.println(mcTag);
			
			payload[j] = rNBTTags.getTagFromMinecraftTag(mcTag);
			
			j++;			
		}
		
		Tag tTileEntities = new Tag(Tag.Type.TAG_List, "TileEntities", payload);
		
		return tTileEntities;		
	}
	
	private Tag getWorldEntities() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		List<Object> entities = world.getLoadedEntities();
		
//		System.out.println("GET " + entities.size());
		
		if (entities.size() == 0)
			return null;
		
		Tag[] payload = new Tag[entities.size()];
		Iterator<Object> i = entities.iterator();
		
		int j = 0;
		while (i.hasNext()) {
			
			Object entity = i.next();
			Object mcTag = rNBTTags.newInstance();
			
			rEntity.getNBTFromEntity(entity, mcTag);

//			System.out.println(mcTag);
			
			payload[j] = rNBTTags.getTagFromMinecraftTag(mcTag);
			j++;
		}
		
		Tag tEntities = new Tag(Tag.Type.TAG_List, "Entities", payload);
		
		return tEntities;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Tag getWorldTileTicks() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		Set tickTicks = world.getPendingTickListEntries();
		int size = tickTicks.size();
		
		if (size == 0)
			return null;
		
		Iterator<Object> tileTicksIterator = tickTicks.iterator();
		
		Tag[] tTileTickArray = new Tag[size];
		int index = 0;
		
		while (tileTicksIterator.hasNext()) {
			
			Object tileTick = tileTicksIterator.next();
			
			Tag tXCoord		= new Tag(Type.TAG_Int, "x", rNextTickListEntry.getXCoord(tileTick));
			Tag tYCoord		= new Tag(Type.TAG_Int, "y", rNextTickListEntry.getYCoord(tileTick));
			Tag tZCoord		= new Tag(Type.TAG_Int, "z", rNextTickListEntry.getZCoord(tileTick));
			Tag tBlock		= new Tag(Type.TAG_Int, "i", rBlock.getIdFromBlock(rNextTickListEntry.getBlock(tileTick)));
			Tag tTime		= new Tag(Type.TAG_Int, "t", (int) (rNextTickListEntry.getScheduledTime(tileTick) - world.getWorldTime()));
			Tag tPriority	= new Tag(Type.TAG_Int, "p", rNextTickListEntry.getPriority(tileTick));
			Tag tEnd		= new Tag(Tag.Type.TAG_End, "", null);
			
			Tag tTileTick = new Tag(Type.TAG_Compound, null, new Tag[]{tXCoord, tYCoord, tZCoord, tBlock, tTime, tPriority, tEnd});
			
			tTileTickArray[index++] = tTileTick;
		}
		
		Tag tTileTicks = new Tag(Type.TAG_List, "TileTicks", tTileTickArray);
		
		return tTileTicks;
	}
	
	/**
	 * Ticks the given world 2 gameticks into the future.
	 * @param id The name of the world.
	 */
	public void tickWorld() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		rWorld.tickUpdates(world, 2l);
//		rWorld.tickEntities(world);
	}
	
	public void onBlockActivated(int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		// TODO 1.8
		
//		Object blockState = rWorld.getBlockState(world, x, y, z);
//		rBlock.onBlockActivated(block, world, x, y, z, null, 0, 0, 0, 0);
	}
	
	public void setBlock(int x, int y, int z, byte blockId, byte blockData) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Object block = rBlock.getBlockById(blockId);
		Object blockState = rBlock.getStateFromMeta(block, blockData);
		
		rWorld.setBlockState(world, x, y, z, blockState, true, true);
	}
	
	public Object getBlockState(int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Object blockState = rWorld.getBlockState(world, x, y, z);
		
		return blockState;
	}
	
	public Object getBlockFromState(Object state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object block = rBlock.getBlockFromState(state);
		
		return block;
	}
	
	public byte getIdFromBlock(Object block) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		byte id = (byte) rBlock.getIdFromBlock(block);
		
		return id;
	}
	
	public byte getDataFromState(Object block, Object state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		byte data = (byte) rBlock.getMetaFromState(block, state);
		
		return data;
	}
	
	public String getNameFromBlock(Object block) throws IllegalArgumentException, IllegalAccessException {
		
		String name = rBlock.getBlockName(block);
		
		return name;
	}
	
	/*
	 * Below here are the things that should not be included in a future library
	 */
	
	public void setState(WorldState state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ArrayIndexOutOfBoundsException, IOException {
		
		rWorld.setWorldTime(world, state.getWorldTime());
		
		setWorldBlocks(world.getxSize(), world.getySize(), world.getzSize(), state.getIds(), state.getData());
		
		world.getLoadedTileEntities().clear();
		world.getLoadedEntities().clear();
		world.getPendingTickListEntries().clear();
		world.getPendingTickListHashSet().clear();
		
		world.getLoadedTileEntities().addAll(state.getTileEntities());
		world.getLoadedEntities().addAll(state.getEntities());
		world.getPendingTickListEntries().addAll(state.getTileTicks());
		world.getPendingTickListHashSet().addAll(state.getTileTickHashes());
	}
	
	public WorldState getState() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		byte[][] blocks = getWorldBlocks();
		
		WorldState state = new WorldState(world.getWorldTime(), blocks[0], blocks[1],
				world.getLoadedTileEntities(), world.getLoadedEntities(),
				world.getPendingTickListEntries(), world.getPendingTickListHashSet());
		
		return state;
	}
}
