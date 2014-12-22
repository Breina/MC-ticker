package sim.logic;

import logging.Log;
import presentation.objects.Block;
import presentation.objects.Entity;
import sim.constants.Constants;
import sim.exceptions.SchematicException;
import sim.objects.WorldInstance;
import sim.objects.WorldState;
import utils.Tag;
import utils.Tag.Type;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	private RBlockPos rBlockPos;
	
	private WorldInstance world;
	
	public SimWorld(RBlock rBlock, RChunk rChunk, RChunkProvider rChunkProvider, REntity rEntity,
			RNBTTags rNBTTags, RNextTickListEntry rNextTickListEntry, RProfiler rProfiler,
			RTileEntity rTileEntity, RWorld rWorld, RChunkPrimer rChunkPrimer, RBlockPos rBlockPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
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
		this.rBlockPos = rBlockPos;
	}
	
	public void createInstance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Log.i("Creating new world");
		world = rWorld.createInstance(Constants.WORLDTYPEID, Constants.WORLDTYPE, Constants.GAMETYPE,
				Constants.SEED, Constants.WORLDPROVIDER, Constants.MAPFEATURESENABLED, Constants.HARDCOREENABLED, rChunk, rChunkProvider,
				rProfiler, Constants.CANSPAWNANIMALS, Constants.CANSPAWNNPCS);
	}
	
	/**
	 * @param worldTypeId Between 0 and 15. Mc uses it like 0=default, 1=flat, 2=largeBiomes, 3=amplified, 8=default_1_1
	 * @param worldType The name of the type of world this is.
	 * @param gameType Should be either "NOT_SET", "SURVIVAL", "CREATIVE" or "ADVENTURE".
	 * @param seed The seed of the world.
	 * @param worldProvider -1=nether, 0=overworld, 1=end.
	 * @param hardcoreEnabled true/false
	 * @param difficulty 0=peaceful 1=easy 2=normal 3=hard
	 */
	public void createInstance(int worldTypeId, String worldType, String gameType, long seed, int worldProvider, boolean hardcoreEnabled, int difficulty, boolean canSpawnAnimals, boolean canSpawnNPCs) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
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

						Log.i("Can spawn animals	  " + canSpawnAnimals);
						Log.i("Can spawn NPCs		  " + canSpawnNPCs);
		
		Log.i("Creating new world");
		world = rWorld.createInstance(worldTypeId, worldType, gameType, seed, worldProvider, Constants.MAPFEATURESENABLED, hardcoreEnabled, rChunk, rChunkProvider, rProfiler, canSpawnAnimals, canSpawnNPCs);
	}
	
	public void createEmptyWorld(int xSize, int ySize, int zSize) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		Block[][][] blocks = new Block[xSize][ySize][zSize];

		for (short y = 0; y < ySize; y++)
			for (short z = 0; z < zSize; z++)
				for (short x = 0; x < xSize; x++)
					blocks[x][y][z] = Block.B_AIR;
			
		setWorldBlocks(xSize, ySize, zSize, blocks);
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

		short xSize = (short) schematicTag.findNextTagByName("Width", null).getValue();
		short ySize = (short) schematicTag.findNextTagByName("Height", null).getValue();
		short zSize = (short) schematicTag.findNextTagByName("Length", null).getValue();

		// Size
		world.setxSize((int) xSize);
		world.setySize((int) ySize);
		world.setzSize((int) zSize);
		
		// Blocks
		byte[] idsArray = (byte[]) schematicTag.findNextTagByName("Blocks", null).getValue();
		byte[] dataArray = (byte[]) schematicTag.findNextTagByName("Data", null).getValue();

		Block[][][] blocks = new Block[world.getxSize()][world.getySize()][world.getzSize()];

		int i = 0;
		for (short y = 0; y < ySize; y++)
			for (short z = 0; z < zSize; z++)
				for (short x = 0; x < xSize; x++) {
					blocks[x][y][z] = new Block(idsArray[i], dataArray[i]);
					i++;
				}
		
		setWorldBlocks(world.getxSize(), world.getySize(), world.getzSize(), blocks);
		
		// TileEntities
		Tag tileEntities = schematicTag.findNextTagByName("TileEntities", null);
		rWorld.clearTileEntities(world);
		if (tileEntities != null)
			setWorldTileEntities((Tag[]) tileEntities.getValue());
		
		// Entities
		Tag entities = schematicTag.findNextTagByName("Entities", null);
		rWorld.clearEntities(world);

		if (entities != null)
			setWorldEntities((Tag[]) entities.getValue());
		
		// TileTicks
		Tag tileTicks = schematicTag.findNextTagByName("TileTicks", null);
		rWorld.clearTickEntries(world);
		if (tileTicks != null)
			setWorldTileTicks((Tag[]) tileTicks.getValue());
	}

	private void setWorldBlocks(int xSize, int ySize, int zSize, Block[][][] blocks) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
		
		rChunkProvider.clear();
		
		int chunksX = (xSize - 1) / 16 + 1;
		int chunksZ = (zSize - 1) / 16 + 1;
		
		// A chunk's height needs to be a quadratic of 2
		int height = 1;
		while (ySize > height)
			height *= 2;
		
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

							// TODO use Block.toShort and check if ids>128 still work
//							short blockId = blocks[worldXoffset + x][y][worldZoffset + z].getId();
//							if (blockId < 0)
//								blockId += 256;
//
//							data[chunkIndex] = (short) (blockId << 4 | blocks[worldXoffset + x][y][worldZoffset + z].getData());

							data[chunkIndex] = blocks[worldXoffset + x][y][worldZoffset + z].toShort();
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
		
		for (Tag tag : tags) {
			
			int x = (int) tag.findTagByName("x").getValue();
			int y = (int) tag.findTagByName("y").getValue();
			int z = (int) tag.findTagByName("z").getValue();
			
			Object mcTag = rNBTTags.getMinecraftTagFromTag(tag);

			Object chunk = rChunkProvider.getChunk(x >> 4, z >> 4);
			Object tileEntity = rTileEntity.createTileEntityFromNBT(mcTag);

			rChunk.addTileEntity(chunk, tileEntity);
		}
	}
	
	private void setWorldEntities(Tag[] tags) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		for (Tag tag : tags) {

			Object mcTag = rNBTTags.getMinecraftTagFromTag(tag);
			
			Object entity = rEntity.createEntityFromNBT(mcTag, world.getWorld());
			
			rWorld.spawnEntityInWorld(world, entity);
		}
	}
	
	private void setWorldTileTicks(Tag[] tags) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		for (Tag tag : tags) {
			
			int xCoord		= (int) tag.findNextTagByName("x", null).getValue();
			int yCoord		= (int) tag.findNextTagByName("y", null).getValue();
			int zCoord		= (int) tag.findNextTagByName("z", null).getValue();
			
			String blockStr	= (String) tag.findNextTagByName("i", null).getValue();
			Object block	= rBlock.getBlockFromName(blockStr);
			
			int time		= (int) tag.findNextTagByName("t", null).getValue();
			int priority	= (int) tag.findNextTagByName("p", null).getValue();
			
			rWorld.addTickEntry(world, xCoord, yCoord, zCoord, block, time, priority);
		}
	}
	
	public void getWorld(OutputStream os) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		getWorldTag().writeTo(os);
		
		Log.i("Saving world");
	}
	
	// TODO do buffering here instead of getting tags out of the sim several times
	public Tag getWorldTag() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {
		
		Block[][][] blocks = getWorldBlocks();

		int size = world.getxSize() * world.getySize() * world.getzSize();

		byte[] ids = new byte[size];
		byte[] data = new byte[size];

		int i = 0;
		for (short y = 0; y < world.getySize(); y++)
			for (short z = 0; z < world.getzSize(); z++)
				for (short x = 0; x < world.getxSize(); x++) {

					Block block = blocks[x][y][z];
					ids[i] = block.getId();
					data[i] = block.getData();
					i++;
				}
		
			Tag tWidth	= new Tag(Tag.Type.TAG_Short, "Width" , (short) world.getxSize());
			Tag tHeight = new Tag(Tag.Type.TAG_Short, "Height", (short) world.getySize());
			Tag tLength = new Tag(Tag.Type.TAG_Short, "Length", (short) world.getzSize());
			
			Tag tMaterials = new Tag(Tag.Type.TAG_String, "Materials", "Alpha");
			Tag tBlocks = new Tag(Tag.Type.TAG_Byte_Array, "Blocks", ids);
			Tag tData = new Tag(Tag.Type.TAG_Byte_Array, "Data", data);
			
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
		
		return tSchematic;
	}
	
	private Block[][][] getWorldBlocks() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		int size = world.getxSize() * world.getySize() * world.getzSize();
		
		Block[][][] blocks = new Block[world.getxSize()][world.getySize()][world.getzSize()];
		
		int i = 0;
		
		if (Constants.DEBUG_SCHEMATIC_DATA)
			System.out.println("id\tx\ty\tz\tindex");
		
		for (int y = 0; y < world.getySize(); y++) {
			for (int z = 0; z < world.getzSize(); z++) {
				for (int x = 0; x < world.getxSize(); x++) {
					
					Object blockState = rWorld.getBlockState(world, x, y, z);
					Object block = rBlock.getBlockFromState(blockState);

					blocks[x][y][z] = new Block((byte) rBlock.getIdFromBlock(block),
							(byte) rBlock.getMetaFromState(block, blockState));
					
					if (Constants.DEBUG_SCHEMATIC_DATA)
						System.out.println(blocks[i] + "\t" + x + "\t" + y + "\t" + z + "\t" + i);
					
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
		
		int j = 0;
		while (i.hasNext()) {
			
			Object mcTileEntity = i.next();
			Object mcTag = rNBTTags.newInstance();
			
			rTileEntity.getNBTFromTileEntity(mcTileEntity, mcTag);
			
			payload[j] = rNBTTags.getTagFromMinecraftTag(mcTag);
			
			j++;			
		}

		return new Tag(Type.TAG_List, "TileEntities", payload);
	}
	
	private Tag getWorldEntities() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		List<Object> entities = world.getLoadedEntities();
		
		if (entities.size() == 0)
			return null;
		
		Tag[] payload = new Tag[entities.size()];
		Iterator<Object> i = entities.iterator();
		
		int j = 0;
		while (i.hasNext()) {
			
			Object entity = i.next();
			Object mcTag = rNBTTags.newInstance();
			
			rEntity.getNBTFromEntity(entity, mcTag);
			
			payload[j] = rNBTTags.getTagFromMinecraftTag(mcTag);
			j++;
		}

		return new Tag(Type.TAG_List, "Entities", payload);
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
			Object blockPos = rNextTickListEntry.getBlockPos(tileTick);
			
			String blockName = "minecraft:" + rBlock.getBlockName(rNextTickListEntry.getBlock(tileTick));
			
			Tag tXCoord		= new Tag(Type.TAG_Int, "x", rBlockPos.getX(blockPos));
			Tag tYCoord		= new Tag(Type.TAG_Int, "y", rBlockPos.getY(blockPos));
			Tag tZCoord		= new Tag(Type.TAG_Int, "z", rBlockPos.getZ(blockPos));
			Tag tBlock		= new Tag(Type.TAG_String, "i", blockName);
			Tag tTime		= new Tag(Type.TAG_Int, "t", (int) (rNextTickListEntry.getScheduledTime(tileTick) - world.getWorldTime()));
			Tag tPriority	= new Tag(Type.TAG_Int, "p", rNextTickListEntry.getPriority(tileTick));
			Tag tEnd		= new Tag(Tag.Type.TAG_End, "", null);
			
			Tag tTileTick = new Tag(Type.TAG_Compound, null, new Tag[]{tXCoord, tYCoord, tZCoord, tBlock, tTime, tPriority, tEnd});
			
			tTileTickArray[index++] = tTileTick;
		}

		return new Tag(Type.TAG_List, "TileTicks", tTileTickArray);
	}

	public boolean tickWorld() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		boolean reachedEnd = !rWorld.tickUpdates(world, 2l);
//		rWorld.tickEntities(world);
//		rWorld.tick(world);

		return reachedEnd;
	}
	
	public void onBlockActivated(int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Object blockState = rWorld.getBlockState(world, x, y, z);

		rBlock.onBlockActivated(blockState, world, x, y, z, 0, 0, 0);
	}
	
	public void setBlock(int x, int y, int z, byte blockId, byte blockData) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Object block = rBlock.getBlockById(blockId);
		Object blockState = rBlock.getStateFromMeta(block, blockData);
		
		rWorld.setBlockState(world, x, y, z, blockState, true, true);
	}
	
	public Object getBlockState(int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		return rWorld.getBlockState(world, x, y, z);
	}
	
	public Object getBlockFromState(Object state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		return rBlock.getBlockFromState(state);
	}
	
	public byte getIdFromBlock(Object block) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		return (byte) rBlock.getIdFromBlock(block);
	}
	
	public byte getDataFromState(Object block, Object state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		return (byte) rBlock.getMetaFromState(block, state);
	}
	
	public String getNameFromBlock(Object block) throws IllegalArgumentException, IllegalAccessException {

		return rBlock.getBlockName(block);
	}
	
	/*
	 * Below here are the things that should not be included in a future library
	 */
	
	public void setState(WorldState state) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ArrayIndexOutOfBoundsException, IOException {
		
		rWorld.setWorldTime(world, state.getWorldTime());
		
		setWorldBlocks(world.getxSize(), world.getySize(), world.getzSize(), state.getBlocks());
		
		world.getLoadedTileEntities().clear();
		world.getLoadedEntities().clear();
		world.getTickableTileEntities().clear();
		world.getPendingTickListEntries().clear();
		world.getPendingTickListHashSet().clear();
		
		world.getLoadedTileEntities().addAll(state.getTileEntities());
		world.getTickableTileEntities().addAll(state.getTickableEntities());
		world.getLoadedEntities().addAll(state.getEntities());
		world.getPendingTickListEntries().addAll(state.getTileTicks());
		world.getPendingTickListHashSet().addAll(state.getTileTickHashes());
	}
	
	public WorldState getState() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Block[][][] blocks = getWorldBlocks();

		return new WorldState(world.getWorldTime(), blocks,
				world.getLoadedTileEntities(), world.getTickableTileEntities(),
				world.getLoadedEntities(), world.getPendingTickListEntries(),
				world.getPendingTickListHashSet());
	}

	public void debug(int x, int y, int z) throws IllegalAccessException, InvocationTargetException, InstantiationException {

		Object tileEntity = rWorld.getTileEntity(world, rBlockPos.createInstance(x, y, z));

		Object mcTag = rNBTTags.newInstance();

		rTileEntity.getNBTFromTileEntity(tileEntity, mcTag);

		Log.i("TileEntity: " + mcTag.toString());
	}

	public Entity[] createViewDataFromEntities(Object[] entities) throws IllegalAccessException {

		Entity[] output = new Entity[entities.length];

		for (int i = 0; i < entities.length; i++) {

			Object entity = entities[i];

			double x = rEntity.getX(entity);
			double y = rEntity.getY(entity);
			double z = rEntity.getZ(entity);
			float width = rEntity.getWidth(entity);
			float height = rEntity.getHeight(entity);
			double vx = rEntity.getMotionX(entity);
			double vy = rEntity.getMotionY(entity);
			double vz = rEntity.getMotionZ(entity);

			output[i] = new Entity(x, y, z, width, height, vx, vy, vz);
		}

		return output;
	}
}
