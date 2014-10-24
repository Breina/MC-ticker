package sim.logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import logging.Log;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import sim.constants.Constants;
import sim.loading.ClassTester;
import sim.loading.Linker;
import sim.objects.WorldInstance;

/**
 * This class is an intermediate between the Simulator's high level logic and all of World's reflection
 */
public class RWorld {
	
	private Class<?> WorldServer, WorldProvider, WorldType, WorldSettings, WorldInfo, IChunkProvider, GameType, World, IntHashMap, BlockPos;
	private Method m_tickUpdates, m_tick, m_getBlockMetadata, m_setBlock, m_setWorldTime, m_getWorldTime,
		m_getProviderForDimension, m_spawnEntityInWorld, m_updateEntities, m_addTickEntry, m_getBlockState, m_setBlockState;
	private Field f_provider, f_levelSaving, f_theProfiler, f_pendingTickListEntriesTreeSet, f_worldInfo, f_chunkProvider, f_isRemote,
	f_worldAccesses, f_loadedEntityList, f_unloadedEntityList, f_playerEntities, f_weatherEffects, f_entityIdMap, f_rand,
	f_pendingTickListEntriesHashSet, f_pendingTickListEntriesThisTick,
	f_lightUpdateBlockList, f_tickableTileEntities, f_loadedTileEntityList, f_addedTileEntityList, f_tileEntitiesToBeRemoved;
	private Constructor<?> c_worldType, c_worldSettings, c_worldInfo, c_blockPos;
	private Enum<?> e_GameType;
	private RBlockPos rBlockPos;
	
	public RWorld(Linker linker, Object profiler, RBlockPos rBlockPos) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		
		prepareWorld(linker, profiler);
		
		this.rBlockPos = rBlockPos;
		
		Log.i("Preparing the World");
	}
	
	/**
	 * Loads up all nessecary things for world to run properly, including the chunkprovider, which we're interfacing
	 */
	public void prepareWorld(Linker linker, Object profiler) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		
		WorldServer							= linker.getClass("WorldServer");
		World								= WorldServer.getSuperclass();
		WorldProvider						= linker.getClass("WorldProvider");
		WorldType							= linker.getClass("WorldType");
		WorldSettings						= linker.getClass("WorldSettings");
		WorldInfo							= linker.getClass("WorldInfo");
		IChunkProvider						= linker.getClass("IChunkProvider");
		IntHashMap							= linker.getClass("IntHashMap");
		BlockPos							= linker.getClass("BlockPos");
		
		Class<?> IBlockState				= linker.getClass("IBlockState");
		Class<?> Block						= linker.getClass("Block");
		
		GameType							= linker.getClass("WorldSettings$GameType");
		
		f_provider							= linker.field("provider", World);
		
		f_levelSaving						= linker.field("disableLevelSaving", WorldServer);
		
		f_pendingTickListEntriesTreeSet		= linker.field("pendingTickListEntriesTreeSet", WorldServer);
		f_pendingTickListEntriesHashSet		= linker.field("pendingTickListEntriesHashSet", WorldServer);
		f_pendingTickListEntriesThisTick	= linker.field("pendingTickListEntriesThisTick", WorldServer);
		
		// TODO 1.8
//		f_entityIdMap						= linker.field("entityIdMap", WorldServer);
		
		f_chunkProvider						= linker.field("chunkProvider", World);
		f_worldInfo							= linker.field("worldInfo", World);
		
		f_isRemote							= linker.field("isRemote", World);
		
		f_worldAccesses						= linker.field("worldAccesses", World);
		f_loadedEntityList					= linker.field("loadedEntityList", World);
		f_unloadedEntityList				= linker.field("unloadedEntityList", World);
		
		f_loadedTileEntityList				= linker.field("loadedTileEntityList", World);
		f_tickableTileEntities				= linker.field("tickableTileEntities", World);
		f_addedTileEntityList				= linker.field("addedTileEntityList", World);
		f_tileEntitiesToBeRemoved			= linker.field("tileEntitiesToBeRemoved", World);
		
		f_playerEntities					= linker.field("playerEntities", World);
		f_weatherEffects					= linker.field("weatherEffects", World);
		f_rand								= linker.field("rand", World);
		f_lightUpdateBlockList				= linker.field("lightUpdateBlockList", World);
		
		c_worldType							= WorldType.getDeclaredConstructor(int.class, String.class);
		c_worldType							.setAccessible(true);
		c_worldSettings						= WorldSettings.getConstructor(long.class, GameType, boolean.class, boolean.class,
													WorldType);
		c_worldInfo							= WorldInfo.getConstructor(WorldSettings, String.class);		
		c_blockPos							= BlockPos.getDeclaredConstructor(int.class, int.class, int.class);
		
		m_getProviderForDimension			= linker.method("getProviderForDimension", WorldProvider, int.class);
		m_tickUpdates						= linker.method("tickUpdates", WorldServer, new Class[]{boolean.class});
		m_tick								= linker.method("tick", WorldServer, new Class[]{});
		
		// TODO 1.8
//		m_getBlockMetadata					= linker.method("getBlockMetadata", World, int.class, int.class, int.class);
//		m_setBlock							= linker.method("setBlock", World, int.class, int.class, int.class,
//													linker.getClass("Block"), int.class, int.class);
		
		m_setWorldTime						= linker.method("func_82738_a", World, long.class );
		m_getWorldTime						= linker.method("getTotalWorldTime", World);
		m_spawnEntityInWorld				= linker.method("spawnEntityInWorld", World, linker.getClass("Entity"));
		m_updateEntities					= linker.method("updateEntities", World);
		
		// TODO can't use linker yet for these
		m_getBlockState						= World.getDeclaredMethod(Constants.WORLD_GETBLOCKSTATE, BlockPos);
		m_setBlockState						= World.getDeclaredMethod(Constants.WORLD_SETBLOCKSTATE, BlockPos, IBlockState, int.class);
		f_theProfiler						= World.getField(Constants.WORLD_THEPROFILER);
		f_theProfiler						.setAccessible(true);
		
		m_addTickEntry						= World.getDeclaredMethod(Constants.WORLD_ADDTICKENTRY, BlockPos, Block, int.class, int.class);
	}
	
	/**
	 * Loads a new world that can be simulated.
	 * @param _worldTypeId Between 0 and 15. Mc uses it like 0=default, 1=flat, 2=largeBiomes, 3=amplified, 8=default_1_1
	 * @param _worldType The name of the type of world this is.
	 * @param _gameType Should be either "NOT_SET", "SURVIVAL", "CREATIVE" or "ADVENTURE".
	 * @param _seed The seed of the world.
	 * @param _worldProvider -1=nether, 0=overworld, 1=end.
	 * @param _mapFeaturesEnabled true/false.
	 * @param _hardcoreEnabled true/false.
	 * @param rChunk we need this to generate an empty chunk.
	 * @param rChunkProvider so we can set its emty chunk with a reference to this world.
	 * @param rProfiler for referencing MC's own profiler for a goldmine of information.
	 * @return A WorldInstance object with all things loaded in it.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WorldInstance createInstance(int _worldTypeId, String _worldType, String _gameType, long _seed, int _worldProvider,
			boolean _mapFeaturesEnabled, boolean _hardcoreEnabled, RChunk rChunk, RChunkProvider rChunkProvider,
			RProfiler rProfiler)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Objenesis objenesis = new ObjenesisStd(false); // <3 I LOVE YOU OBJENESIS <3
		
		Object worldServer = objenesis.newInstance(WorldServer);
		
		Object worldProvider = m_getProviderForDimension.invoke(null, _worldProvider);
		f_provider.set(worldServer, worldProvider);
		
		f_levelSaving.setBoolean(worldServer, true);
		
		f_theProfiler.set(worldServer, rProfiler.getInstance());
		
		TreeSet<Object> pendingTickListEntriesTreeSet = new TreeSet<>();
		HashSet<Object> pendingTickListEntriesHashSet = new HashSet<>();
		
		f_pendingTickListEntriesTreeSet.set(worldServer, pendingTickListEntriesTreeSet);
		f_pendingTickListEntriesHashSet.set(worldServer, pendingTickListEntriesHashSet);
		f_pendingTickListEntriesThisTick.set(worldServer, new ArrayList<Object>());
		
		Object worldType = c_worldType.newInstance(_worldTypeId, _worldType);
		e_GameType = Enum.valueOf((Class<Enum>) GameType, _gameType);
		
		Object worldSettings = c_worldSettings.newInstance(_seed, e_GameType, _mapFeaturesEnabled, _hardcoreEnabled, worldType);
		
		Object worldInfo = c_worldInfo.newInstance(worldSettings, Constants.APPNAME);
		f_worldInfo.set(worldServer, worldInfo);
		
		Object chunkProvider = Proxy.newProxyInstance(IChunkProvider.getClassLoader(),
				new Class[]{IChunkProvider}, rChunkProvider);		
		
		f_chunkProvider.set(worldServer, chunkProvider);
		
		// TODO 1.8 (Make this available)
//		Object entityIdMap = IntHashMap.newInstance();
//		f_entityIdMap.set(worldServer, entityIdMap);
		
		// TODO Make this available
		ArrayList<Object> loadedTileEntities = new ArrayList<>();
		ArrayList<Object> loadedEntities = new ArrayList<>();
		
		f_isRemote.setBoolean(worldServer, false);
		
		f_worldAccesses.set(worldServer, new ArrayList<>()); // No one's listening, go home worldAccesses
		f_loadedEntityList.set(worldServer, loadedEntities);
		f_unloadedEntityList.set(worldServer, new ArrayList<>());
		f_loadedTileEntityList.set(worldServer, loadedTileEntities);
		f_tickableTileEntities.set(worldServer, new ArrayList<>());
		f_addedTileEntityList.set(worldServer, new ArrayList<>());
		f_tileEntitiesToBeRemoved.set(worldServer, new ArrayList<>());
		f_playerEntities.set(worldServer, new ArrayList<>());
		f_weatherEffects.set(worldServer, new ArrayList<>());
		
		// TODO might want to play with this
		f_rand.set(worldServer, new Random());
		
		f_lightUpdateBlockList.set(worldServer, new int[32768]);
		
		rChunkProvider.setEmptyChunk(rChunk.generateEmptyChunk(worldServer));
		
		WorldInstance world = new WorldInstance();
			world.setWorld(worldServer);
			world.setLoadedTileEntities(loadedTileEntities);
			world.setLoadedEntities(loadedEntities);
			world.setPendingTickListEntries(pendingTickListEntriesTreeSet);
			world.setPendingTickListHashSet(pendingTickListEntriesHashSet);
			world.setDoTimeUpdate(true);
		return world;
	}
	
	
	public void tick(WorldInstance world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_tick.invoke(world.getWorld());
		
		world.setDoTimeUpdate(true);
	}
	
	/**
	 * This invokes the relevant function once
	 */
	public void tickUpdates(WorldInstance world, long advanceTicks) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if (Constants.DEBUG_WORLD)
			System.out.println("Ticking " + world.getPendingTickListEntries().size() + " pending updates...");
		
		advanceTicks(world, advanceTicks);
		
		m_tickUpdates.invoke(world.getWorld(), false);
	}
	
	public void tickEntities(WorldInstance world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_updateEntities.invoke(world.getWorld());
	}
	
	public void advanceTicks(WorldInstance world, long amount) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if (amount == 0l)
			return;
		
		long time = getWorldTime(world);
		
		time += amount;
		
		setWorldTime(world, time);
	}
	
	public void setWorldTime(WorldInstance world, long time) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if (Constants.DEBUG_WORLD)
			System.out.println("Changing time form " + world.getWorldTime() + " to " + time);
		
		m_setWorldTime.invoke(world.getWorld(), time);
		
		world.setWorldTime(time);
	}
	
	public long getWorldTime(WorldInstance world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if (world.doTimeUpdate()) {
			
			world.setWorldTime((long) m_getWorldTime.invoke(world.getWorld()));
			world.setDoTimeUpdate(false);
			
		}
		
		return world.getWorldTime();
	}
	
	public boolean spawnEntityInWorld(WorldInstance world, Object entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		boolean succes = (boolean) m_spawnEntityInWorld.invoke(world.getWorld(), entity);
		
		return succes;		
	}
	
	public Object getBlockState(WorldInstance world, int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		Object blockState = m_getBlockState.invoke(world.getWorld(), rBlockPos.createInstance(x, y, z));
		
		return blockState;
	}
	
	public boolean setBlockState(WorldInstance world, int x, int y, int z, Object blockState, boolean update, boolean sendChange) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		int flags = 4 | (sendChange ? 2 : 0) | (update ? 1 : 0);
		
		boolean succes = (boolean) m_setBlockState.invoke(world.getWorld(), rBlockPos.createInstance(x, y, z), blockState, flags);
		
		if (!succes)
			Log.w("Set block: no changes");
		
		return succes;
	}
	
	/**
	 * Returns the hashset containing NextTickListEntry objects
	 * @return
	 */
	public Object getPendingTicks(WorldInstance world) {
		
		return world.getPendingTickListEntries();
	}
	
	public List<Object> getLoadedTileEntities(WorldInstance world) throws IllegalArgumentException, IllegalAccessException {
		
		List<Object> tileEntities = (List<Object>) f_loadedTileEntityList.get(world.getWorld());
		
		return tileEntities;
	}
	
	public void addTickEntry(WorldInstance world, int x, int y, int z, Object block, int scheduledTime, int priority) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		m_addTickEntry.invoke(world.getWorld(), rBlockPos.createInstance(x, y, z), block, scheduledTime, priority);
	}
	
	public void clearTileEntities(WorldInstance world) {
		world.clearLoadedTileEntities();
	}
	
	public void clearEntities(WorldInstance world) {
		world.clearLoadedEntities();
	}
	
	public void clearTickEntries(WorldInstance world) {
		world.clearPendingTickLists();
	}
}
