package sim.constants;

public class Constants {

	public final static boolean DEBUG_TILE_IMAGES = false;
	public final static boolean DEBUG_SWING = false;
	public final static boolean DEBUG_WORLD = false;
	public final static boolean DEBUG_CHUNKPROVIDER = false;
	public final static boolean DEBUG_MC_SCHEMATICS = false;
	public final static boolean DEBUG_SCHEMATIC_DATA = false;
	public final static boolean DEBUG_TAG_COMPOUND = false;
    public final static boolean DEBUG_SKIP_LOADING = false;

	public final static boolean LOG_IGNORE_WARNINGS = true;
	
	public final static int OUTPUT_INDENT = 90;
	
	// The name that will be returned should it ever be called
	public final static String CHUNKPROVIDERSTRING = "SIM chunkprovider";
	public final static String PLAYERNAME = "SIM player";
	
	// Files | folders
	public final static String JOINEDSRG = "joined.srg";
	public final static String METHODSCSV = "methods.csv";
	public final static String FIELDSCSV = "fields.csv";
	public final static String VERSION = "version.cfg";		// unused
	
	// Z:/MCP/conf
	public final static String MCPCONFFOLDER = "mcp/conf";

	public final static String MINECRAFTFOLDER = "/.minecraft";
	public final static String LIBRARYFOLDER = "/libraries";
	public final static String MINECRAFTJAR = "/versions/1.8/1.8.jar";

	/**
	 * The following 2 final arrays are 2D arrays, this is however an option. The second array element is
	 * checked if it is contained within its description. This is used to exclude duplicates. 
	 */
	public final static String[][] REQUIREDMETHODS = {
		{"tickUpdates"}, {"getProviderForDimension"}, {"registerBlocks"}, {"getBlockById"},
		{"getStateFromMeta", "Convert the given metadata"}, {"getIdFromBlock"},
		{"startSection"}, {"endSection"}, {"setWorldTime", "ets the wo"},
		{"generateSkylightMap"}, {"getTotalWorldTime"}, {"hasTileEntity"}, {"createAndLoadEntity"}, {"addTileEntity"},
		{"write", "Write the actual data"}, {"createEntityFromNBT"}, {"writeToNBT","Save the entity"}, {"spawnEntityInWorld"},
		{"onChunkLoad", "Called when this Chunk"}, {"getItemById"}, {"registerItems"},
		{"getUnlocalizedName", ""}, {"getTagList"}, {"getCompoundTagAt"},
		{"onBlockActivated"}, {"register", "Registers blocks, items"}, {"getBlockState"}, {"getMetaFromState"}, {"getBlockFromName"},
		{"incrementTotalWorldTime"}, {"create","PropertyDirection with the given name"}, {"getValue", "Get the value"},
		{"getProperties", "Get all properties"}, {"getTileEntity"}, {"update", "Updates the JList"},
		{"onUpdate", "Called to update the entity"}, {"clearMap"}, {"getNameForObject"},
		{"getEntityString", "Returns the string that identifies"}, {"getEventID"}, {"getEventParameter"},
		{"onBlockEventReceived"}};

	public final static String[][] REQUIREDFIELDS = {
		{"provider"}, {"disableLevelSaving"}, {"pendingTickListEntriesTreeSet"}, {"pendingTickListEntriesHashSet"},
		{"pendingTickListEntriesThisTick"}, {"worldInfo"}, {"chunkProvider"}, {"blockRegistry"},
		{"profilingMap"}, {"profilingEnabled"}, {"worldAccesses"}, {"isRemote"}, {"loadedEntityList"},
		{"unloadedEntityList"}, {"playerEntities"}, {"weatherEffects"}, {"worldInfo"},
		{"entitiesById"}, {"entitiesByUuid"}, {"rand", "RNG for World."}, {"scheduledTime"},
		{"lightUpdateBlockList"}, {"tickableTileEntities"}, {"loadedTileEntityList"}, {"worldBorder"},
		{"addedTileEntityList"}, {"tileEntitiesToBeRemoved"}, {"x", "X coor"}, {"y", "Y coor"}, {"z", "Z coor"},
		{"posX", "Entity"}, {"posY", "Entity"}, {"posZ", "Entity"}, {"width", "How wide"}, {"isDead"},
		{"motionX", "Entity"}, {"motionY", "Entity"}, {"motionZ", "Entity"}, {"height", "How high"}, {"entityUniqueID"},
        {"blockEventCacheIndex"}};

	public final static String[] REQUIREDCLASSES = {"World", "WorldServer", "WorldProvider", "Profiler", "WorldSettings",
		"WorldSettings$GameType", "WorldInfo", "WorldType", "IChunkProvider", "Block", "Chunk",
		"NBTTagCompound", "TileEntity", "IntHashMap", "Entity", "EntityList", "Bootstrap", "NBTTagList",
		"NBTSizeTracker", "NextTickListEntry", "EntityPlayer", "EntityOtherPlayerMP", "ChunkPrimer", "BlockPos", "IBlockState", "Vec3i", "BlockPos$MutableBlockPos",
		"EnumFacing", "PropertyDirection", "IProperty", "WorldBorder", "WorldServer$ServerBlockEventList",
		"IUpdatePlayerListBox", "RegistryNamespaced", "BlockEventData"};

	// Untranslated methods
	public final static String WORLD_GETBLOCKSTATE = "p";
	public final static String WORLD_SETBLOCKSTATE = "a";
	public final static String IBLOCKSTATE_GETBLOCK = "c";
	public final static String WORLD_ADDTICKENTRY = "b";
    public final static String TILEENTITY_READFROMNBT = "b";
    public final static String NBTTAGCOMPOUND_LOAD = "a";
	public final static String BLOCKEVENTDATA_GETBLOCKPOS = "a";

    // Untranslated fields
	public final static String CHUNKPRIMER_DATA = "a";
    public final static String WORLD_THEPROFILER = "B";
    public final static String NEXTTICKLISTENTRY_BLOCK = "e";
    public final static String NEXTTICKLISTENTRY_BLOCKPOS = "a";
    public final static String NEXTTICKLISTENTRY_PRIORITY = "c";
    public final static String BLOCK_UNLOCALIZEDNAME = "N";
	public final static String WORLDSERVER_SERVERBLOCKEVENTLIST = "S";

    // Classes that need to be gotten out of a library {className, package, libPath}
    public static final String[][] LIBRARYCLASSES = {{"GameProfile", "com/mojang/authlib/", "/com/mojang/authlib/1.5.17/authlib-1.5.17.jar"}};

    // Properties of the World object
	public final static String WORLDTYPE = "Simulation";
    public final static int WORLDTYPEID = 1;
    public final static String APPNAME = "Simulator";
    public final static String GAMETYPE = "CREATIVE";
    public final static long SEED = 420l;
    public final static boolean MAPFEATURESENABLED = false;
    public final static boolean HARDCOREENABLED = false;
    public final static int WORLDPROVIDER = 0;
}