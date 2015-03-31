package sim.logic;

import logging.Log;
import presentation.objects.Block;
import presentation.objects.Entity;
import sim.constants.Constants;
import sim.objects.WorldInstance;
import utils.Tag;
import utils.Tag.Type;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SimWorld {
	
	private final RWorld rWorld;
	private final RBlock rBlock;
	private final RChunk rChunk;
	private final RProfiler rProfiler;
	private final RChunkProvider rChunkProvider;
	private final RTileEntity rTileEntity;
	private final RNBTTags rNBTTags;
	private final REntity rEntity;
	private final RNextTickListEntry rNextTickListEntry;
	private final RChunkPrimer rChunkPrimer;
	private final RBlockPos rBlockPos;

    private WorldInstance world;

	private Tag cachedSchematic;
	private boolean isSchematicUpToDate;
	
	public SimWorld(RBlock rBlock, RChunk rChunk, REntity rEntity,
			RNBTTags rNBTTags, RNextTickListEntry rNextTickListEntry, RProfiler rProfiler,
			RTileEntity rTileEntity, RWorld rWorld, RChunkPrimer rChunkPrimer, RBlockPos rBlockPos) throws IllegalArgumentException {
		
		this.rBlock = rBlock;
		this.rChunk = rChunk;
		this.rChunkProvider = new RChunkProvider(rBlockPos);
		this.rEntity = rEntity;
		this.rNBTTags = rNBTTags;
		this.rNextTickListEntry = rNextTickListEntry;
		this.rProfiler = rProfiler;
		this.rTileEntity = rTileEntity;
		this.rWorld = rWorld;
		this.rChunkPrimer = rChunkPrimer;
		this.rBlockPos = rBlockPos;

        isSchematicUpToDate = false;
	}
	
	public void createInstance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Log.i("Creating new world");
		world = rWorld.createInstance(Constants.WORLDTYPEID, Constants.WORLDTYPE, Constants.GAMETYPE,
				Constants.SEED, Constants.WORLDPROVIDER, Constants.MAPFEATURESENABLED, Constants.HARDCOREENABLED, rChunk, rChunkProvider,
				rProfiler);
	}
	
	/**
     * @param worldTypeId Between 0 and 15. Mc uses it like 0=default, 1=flat, 2=largeBiomes, 3=amplified, 8=default_1_1
     * @param worldType The name of the type of world this is.
     * @param gameType Should be either "NOT_SET", "SURVIVAL", "CREATIVE" or "ADVENTURE".
     * @param seed The seed of the world.
     * @param worldProvider -1=nether, 0=overworld, 1=end.
     * @param hardcoreEnabled true/false
     */
	public void createInstance(int worldTypeId, String worldType, String gameType, long seed, int worldProvider, boolean hardcoreEnabled) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		world = rWorld.createInstance(worldTypeId, worldType, gameType, seed, worldProvider, Constants.MAPFEATURESENABLED, hardcoreEnabled, rChunk, rChunkProvider, rProfiler);
	}
	
	public void createEmptyWorld(int xSize, int ySize, int zSize) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		Block[][][] blocks = new Block[xSize][ySize][zSize];

		for (short y = 0; y < ySize; y++)
			for (short z = 0; z < zSize; z++)
				for (short x = 0; x < xSize; x++)
					if (y != 0)
						blocks[x][y][z] = Block.B_AIR;
					else
						blocks[x][y][z] = Block.B_SOLID;
			
		setBlockObjects(xSize, ySize, zSize, blocks);
		world.setxSize(xSize);
		world.setySize(ySize);
		world.setzSize(zSize);
	}

	public void setSchematic(InputStream input) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchAlgorithmException {
		
		Tag schematicTag = Tag.readFrom(input);
		
		if (Constants.DEBUG_MC_SCHEMATICS) {
			System.out.println("SET");
			schematicTag.print();
		}
		
		setSchematic(schematicTag);
	}
	
	public void setSchematic(Tag schematicTag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ArrayIndexOutOfBoundsException, IOException {

		this.cachedSchematic = schematicTag;
		isSchematicUpToDate = true;

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
		
		setBlockObjects(world.getxSize(), world.getySize(), world.getzSize(), blocks);

		world.clearLists();
		
		// TileEntities
		Tag tileEntities = schematicTag.findNextTagByName("TileEntities", null);
		if (tileEntities != null)
			setTileEntities((Tag[]) tileEntities.getValue());
		
		// Entities
		Tag entities = schematicTag.findNextTagByName("Entities", null);

		if (entities != null)
			setEntities((Tag[]) entities.getValue());
		
		// TileTicks
		Tag tileTicks = schematicTag.findNextTagByName("TileTicks", null);
		if (tileTicks != null)
			setTileTicks((Tag[]) tileTicks.getValue());
	}

	private void setBlockObjects(int xSize, int ySize, int zSize, Block[][][] blocks) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		
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
	private void setTileEntities(Tag[] tags) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
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
	
	private void setEntities(Tag[] tags) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		for (Tag tag : tags) {

			Object mcTag = rNBTTags.getMinecraftTagFromTag(tag);
			
			Object entity = rEntity.createEntityFromNBT(mcTag, world.getWorld());

			if (entity == null) {
				Log.e("Spawning entity failed");
				continue;
			}
			
			rWorld.spawnEntityInWorld(world, entity);
		}
	}
	
	private void setTileTicks(Tag[] tags) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
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
	
	public void getSchematic(OutputStream os) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		getSchematic().writeTo(os);
		
		Log.i("Saving world");
	}

	public Tag getSchematic() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, IOException {

		if (isSchematicUpToDate)
			return cachedSchematic;

		Block[][][] blocks = getBlockObjects();

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
			Tag tTileEntities = getTileEntities();
			Tag tEntities = getEntities();
			Tag tTileTicks = getTileTicks();
			
			Tag tEnd = new Tag(Tag.Type.TAG_End, "", null);

		cachedSchematic = new Tag(Tag.Type.TAG_Compound, "Schematic", new Tag[]{tHeight, tLength, tWidth, tMaterials, tData, tBlocks, tEnd});
		
		if (tTileEntities != null)
			cachedSchematic.addTag(tTileEntities);
		
		if (tEntities != null)
			cachedSchematic.addTag(tEntities);
		
		if (tTileTicks != null)
			cachedSchematic.addTag(tTileTicks);

		cachedSchematic.addTag(tEnd);
	
		if (Constants.DEBUG_MC_SCHEMATICS) {
			System.out.println("GET:");
			cachedSchematic.print();
		}

		isSchematicUpToDate = true;
		
		return cachedSchematic;
	}
	
	private Tag getTileEntities() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {

		List<Object> tileEntities = rWorld.getLoadedTileEntities(world);

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

	private Tag getEntities() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		List<Object> entities = world.getLoadedEntities();

		if (entities.size() == 0)
			return null;

		Tag[] payload = new Tag[entities.size()];
		Iterator<Object> i = entities.iterator();

		int j = 0;
		while (i.hasNext()) {

			Object entity = i.next();
			Object mcTag = rEntity.getNBTFromEntity(entity);

			Tag tEntity = rNBTTags.getTagFromMinecraftTag(mcTag);

			// This will save dead entities as well
			tEntity.addTag(new Tag(Type.TAG_String, "id", rEntity.getEntityString(entity)));
            tEntity.addTag(new Tag(Type.TAG_String, "UUID", rEntity.getUUID(entity).toString()));

			payload[j] = tEntity;
			j++;
		}

		return new Tag(Type.TAG_List, "Entities", payload);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Tag getTileTicks() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

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

			String blockName = rBlock.getInternalBlockName(rNextTickListEntry.getBlock(tileTick));

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

		isSchematicUpToDate = false;

        return !rWorld.tick(world, 1l);
	}

	public void onBlockActivated(int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		isSchematicUpToDate = false;

		Object blockState = rWorld.getBlockState(world, x, y, z);

		rBlock.onBlockActivated(blockState, world, x, y, z, 0, 0, 0);
	}

	public void setBlock(int x, int y, int z, byte blockId, byte blockData) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		isSchematicUpToDate = false;

		Object block = rBlock.getBlockById(blockId);
		Object blockState = rBlock.getStateFromMeta(block, blockData);

		rWorld.setBlockState(world, x, y, z, blockState, true, true);
	}

	public Object getBlockState(int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		return rWorld.getBlockState(world, x, y, z);
	}

    // TODO sometime in the future, I'd like the things that don't have to do with world to move to their own block class.
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

		return rBlock.getReadableBlockName(block);
	}

    public boolean isOpaque(Object block) throws InvocationTargetException, IllegalAccessException {

        return rBlock.isOpaque(block);
    }

    public boolean isFullCube(Object block) throws InvocationTargetException, IllegalAccessException {

        return rBlock.isFullCube(block);
    }

	public void debug(int x, int y, int z) throws IllegalAccessException, InvocationTargetException, InstantiationException {

		isSchematicUpToDate = false;

		Object tileEntity = rWorld.getTileEntity(world, rBlockPos.createInstance(x, y, z));

		Object mcTag = rNBTTags.newInstance();

		rTileEntity.getNBTFromTileEntity(tileEntity, mcTag);

		Log.i("TileEntity: " + mcTag.toString());
	}

    // TODO I do not like this getting called twice, try to avoid it or cache this shit
	public Block[][][] getBlockObjects() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

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

	public Entity[] getEntityObjects() throws IllegalAccessException, InvocationTargetException {

		List<Object> entities = world.getLoadedEntities();
		Iterator<Object> entityIterator = entities.iterator();

		Entity[] output = new Entity[entities.size()];

		int index = 0;

		while (entityIterator.hasNext()) {

			Object entity = entityIterator.next();

			double x = rEntity.getX(entity);
			double y = rEntity.getY(entity);
			double z = rEntity.getZ(entity);
			float width = rEntity.getWidth(entity);
			float height = rEntity.getHeight(entity);
			double vx = rEntity.getMotionX(entity);
			double vy = rEntity.getMotionY(entity);
			double vz = rEntity.getMotionZ(entity);
			boolean isDead = rEntity.isDead(entity);
			String id = rEntity.getEntityString(entity);
            UUID uuid = rEntity.getUUID(entity);

			output[index++] = new Entity(x, y, z, width, height, vx, vy, vz, isDead, id, uuid);
		}

		return output;
	}

	public long getWorldTime() {
		return world.getWorldTime();
	}
}
