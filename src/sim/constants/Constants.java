package sim.constants;

public class Constants {
	
	public final static boolean DEBUG_WORLD = false;
	public final static boolean DEBUG_CHUNKPROVIDER = false;
	public final static boolean DEBUG_MC_SCHEMATICS = false;
	public final static boolean DEBUG_TAG_SCHEMATICS = false;
	public final static boolean DEBUG_SCHEMATIC_DATA = false;
	public final static boolean DEBUG_TAG_COMPOUND = false;
	
	public final static boolean LOG_IMMEDIATELY = true;
	public final static boolean LOG_AFTERWARDS = false;
	public final static boolean LOG_IGNORE_WARNINGS = true;
	
	public final static int OUTPUT_INDENT = 90;
	
	// The name that will be returned should it ever be called
	public final static String CHUNKPROVIDERSTRING = "SIM chunkprovider";
	
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
		{"setBlock", "Sets the block ID and metadata at a given location"}, {"startSection"}, {"endSection"},
		{"generateSkylightMap"}, {"tick", "Runs a single tick for the world"}, {"func_82738_a"},
		{"getTotalWorldTime"}, {"hasTileEntity"}, {"createAndLoadEntity"}, {"addTileEntity"},
		{"write", "Write the actual data"}, {"createEntityFromNBT"}, {"writeToNBT", "Save the entity to NBT"}, {"spawnEntityInWorld"},
		{"updateEntities"}, {"onChunkLoad", "Called when this Chunk"}, {"getIconString"}, {"getItemById"}, {"registerItems"},
		{"getInventoryName"}, {"getUnlocalizedName", ""}, {"getStackInSlot"}, {"getSizeInventory"}, {"getTagList"}, {"getCompoundTagAt"},
		{"loadItemStackFromNBT"}, {"readFromNBT", "Read the stack fields"}, {"onBlockActivated"},
		{"register", "Registers blocks, items"}, {"getBlockState"}, {"getMetaFromState"}};
	
	public final static String[][] REQUIREDFIELDS = {
		{"provider"}, {"disableLevelSaving"}, {"pendingTickListEntriesTreeSet"}, {"pendingTickListEntriesHashSet"},
		{"pendingTickListEntriesThisTick"}, {"worldInfo"}, {"chunkProvider"},
		{"unlocalizedNameBlock"}, {"profilingMap"}, {"profilingEnabled"}, {"worldAccesses"}, {"isRemote"}, {"loadedEntityList"},
		{"unloadedEntityList"}, {"playerEntities"}, {"weatherEffects"},
		{"entityIdMap"}, {"rand", "RNG for World."}, {"xCoord", "X position this tick is occuring at"},
		{"yCoord", "Y position this tick is occuring at"}, {"zCoord", "Z position this tick is occuring at"}, {"scheduledTime"},
		{"priority"}, {"lightUpdateBlockList"}, {"tickableTileEntities"}, {"loadedTileEntityList"},
		{"addedTileEntityList"}, {"tileEntitiesToBeRemoved"}};

	public final static String[] REQUIREDCLASSES = {"World", "WorldServer", "WorldProvider", "Profiler", "WorldSettings",
		"WorldSettings$GameType", "WorldInfo", "WorldType", "IChunkProvider", "Block", "Chunk",
		"NBTTagCompound", "TileEntity", "IntHashMap", "Entity", "EntityList", "Bootstrap", "IInventory", "ItemStack", "NBTTagList",
		"NBTSizeTracker", "NextTickListEntry", "EntityPlayer", "ChunkPrimer", "BlockPos", "IBlockState"};
	
	// Untranslated methods (new)
	public final static String WORLD_GETBLOCKSTATE = "p";
	public final static String WORLD_SETBLOCKSTATE = "a";
	public final static String IBLOCKSTATE_GETBLOCK = "c";
	
	// Untranslated methods (old)
	public final static String WORLD_ADDTICKENTRY = "b";
	public final static String NBTTAGCOMPOUND_LOAD = "a"; // Used to be {"load", "Read the actual data"} 
	
	// Untranslated fields (new)
	public final static String CHUNKPRIMER_DATA = "a";
	public final static String WORLD_THEPROFILER = "B"; 
	
	// Untranslated fields (old)
	public final static String NEXTTICKLISTENTRY_BLOCK = "g";
	
	// Properties of the World object
	public final static String WORLDTYPE = "Simulation";
	public final static int WORLDTYPEID = 1;
	public final static String APPNAME = "Simulator";
	public final static String GAMETYPE = "CREATIVE";
	public final static long SEED = 420l;
	public final static boolean MAPFEATURESENABLED = false;
	public final static boolean HARDCOREENABLED = false;
	public final static int WORLDPROVIDER = 0; // -1=nether, 0=overworld, 1=end
	
	// Unused and I think we'll maybe need these in the future
	public final static int DIFFICULTY = 0;
		
	public final static String LOGENTRY = "[SIM]";
	public final static String LOGFILE = "log.txt";
}

// UNUSED CODE THAT MAY BE HANDY IN THE FUTURE:


//public static void removeFinalField(Field field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//    field.setAccessible(true);
//
//    // remove final modifier from field
//    Field modifiersField = Field.class.getDeclaredField("modifiers");
//    modifiersField.setAccessible(true);
//    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//}


//public String lookupMethod(String obfuscated) {
//	
//	String key = null;
//	
//	if (!reversedMethods.containsKey(obfuscated)) {
//		
//		if (!methods.containsValue(obfuscated))
//			return null;
//			
//		Iterator<Entry<String, String>> i = methods.entrySet().iterator();
//		
//		while (i.hasNext()) {
//			
//			Entry<String, String> entry = i.next();
//			
//			System.out.println("comparing " + obfuscated + "=" + entry.getValue());
//			
//			if (!entry.getValue().equals(obfuscated))
//				continue;
//			
//			key = entry.getKey();
////			methods.remove(key);	// TODO throws a concurrentModificationException
//			
//			reversedMethods.put(obfuscated, key);
//		}
//	} else {
//		
//		key = reversedMethods.get(obfuscated);
//		
//	}
//	
//	return key;
//}

//getworld
//int minX = 16 * rChunkProvider.getMinX();
//int maxX = 16 * rChunkProvider.getMaxX();
//int minZ = 16 * rChunkProvider.getMinZ();
//int maxZ = 16 * rChunkProvider.getMaxZ();
//
//int xSize = maxX - minX;
//int zSize = maxZ - minZ;
//
//byte[][][] blockIds = new byte[xSize][128][zSize];
//byte[][][] blockData = new byte[xSize][128][zSize];
//
//int y;
//
//for (y = 0; y < 128; y++) {
//	
//	boolean foundNotAir = false;
//	
//	for (int x = minX; x < maxX; x++) {
//		for (int z = minZ; z < maxZ; z++) {
//			
//			byte blockId = (byte) rBlock.getIdFromBlock(rWorld.getBlock(x, y, z));
//			blockIds[x][y][z] = blockId;
//			blockData[x][y][z] = (byte) rWorld.getBlockMetaData(x, y, z);
//			
//			if (blockId != 0)
//				foundNotAir = true;
//		}
//	}
//	
//	if (!foundNotAir) {
//		y--;
//		break;
//	}
//}
//
//int maxY = y;
//
//SharedWorld sharedWorld = new SharedWorld();
//
//return null;