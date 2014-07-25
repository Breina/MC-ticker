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
import sim.loading.Linker;

/**
 * This class is an intermediate between the Simulator's high level logic and all of World's reflection
 */
public class RWorld implements ISimulated {
	
	private Class<?> WorldServer, WorldProvider, WorldType, WorldSettings, WorldInfo, IChunkProvider, GameType, World, IntHashMap;
	private Method m_tickUpdates, m_tick, m_getBlock, m_getBlockMetadata, m_setBlock, m_setWorldTime, m_getWorldTime,
		m_getProviderForDimension, m_spawnEntityInWorld, m_updateEntities, m_addTickEntry;
	private Field f_provider, f_levelSaving, f_theProfiler, f_pendingTickListEntriesTreeSet, f_worldInfo, f_chunkProvider, f_isClient,
	f_worldAccesses, f_loadedEntityList, f_unloadedEntityList, f_loadedTileEntities, f_toLoadTileEntities, f_toUnloadTileEntities,
	f_playerEntities, f_weatherEffects, f_entityIdMap, f_rand, f_pendingTickListEntriesHashSet, f_pendingTickListEntriesThisTick;
	private Constructor<?> c_worldType, c_worldSettings, c_worldInfo;
	private Enum<?> e_GameType;
	
	public RWorld(Linker linker, Object profiler) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		
		prepareWorld(linker, profiler);
		
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
		
		GameType							= linker.getClass("WorldSettings$GameType");
		
		f_provider							= linker.field("provider", World);			
		f_levelSaving						= linker.field("levelSaving", WorldServer);
		f_pendingTickListEntriesTreeSet		= linker.field("pendingTickListEntriesTreeSet", WorldServer);
		f_pendingTickListEntriesHashSet		= linker.field("pendingTickListEntriesHashSet", WorldServer);
		f_pendingTickListEntriesThisTick	= linker.field("pendingTickListEntriesThisTick", WorldServer);
		f_entityIdMap						= linker.field("entityIdMap", WorldServer);
		f_chunkProvider						= linker.field("chunkProvider", World);
		f_worldInfo							= linker.field("worldInfo", World);
		f_isClient							= linker.field("isClient", World);
		f_worldAccesses						= linker.field("worldAccesses", World);
		f_loadedEntityList					= linker.field("loadedEntityList", World);
		f_unloadedEntityList				= linker.field("unloadedEntityList", World);
		f_loadedTileEntities				= linker.field("field_147482_g", World);
		f_toLoadTileEntities				= linker.field("field_147484_a", World);
		f_toUnloadTileEntities				= linker.field("field_147483_b", World);
		f_playerEntities					= linker.field("playerEntities", World);
		f_weatherEffects					= linker.field("weatherEffects", World);
		f_rand								= linker.field("rand", World);
		
		c_worldType							= WorldType.getDeclaredConstructor(int.class, String.class);
		c_worldType							.setAccessible(true);
		c_worldSettings						= WorldSettings.getConstructor(long.class, GameType, boolean.class, boolean.class,
													WorldType);
		c_worldInfo							= WorldInfo.getConstructor(WorldSettings, String.class);		
		
		m_getProviderForDimension			= linker.method("getProviderForDimension", WorldProvider, int.class);
		m_tickUpdates						= linker.method("tickUpdates", WorldServer, new Class[]{boolean.class});
		m_tick								= linker.method("tick", WorldServer, new Class[]{});
		m_getBlockMetadata					= linker.method("getBlockMetadata", World, int.class, int.class, int.class);
		m_setBlock							= linker.method("setBlock", World, int.class, int.class, int.class,
													linker.getClass("Block"), int.class, int.class);
		m_setWorldTime						= linker.method("func_82738_a", World, long.class );
		m_getWorldTime						= linker.method("getTotalWorldTime", World);
		m_spawnEntityInWorld				= linker.method("spawnEntityInWorld", World, linker.getClass("Entity"));
		m_updateEntities					= linker.method("updateEntities", World);
		
		// TODO can't use linker yet for these
		m_getBlock							= World.getDeclaredMethod(Constants.WORLD_GETBLOCK, int.class, int.class, int.class);
		m_addTickEntry						= World.getDeclaredMethod(Constants.WORLD_ADDTILEENTRY, int.class, int.class, int.class, linker.getClass("Block"), int.class, int.class);
		f_theProfiler						= World.getField(Constants.WORLD_PROFILER);
		f_theProfiler						.setAccessible(true);
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
		f_levelSaving.setBoolean(worldServer, false);
		f_theProfiler.set(worldServer, rProfiler.getInstance());
		
		TreeSet<?> pendingTickListEntriesTreeSet = new TreeSet<>();
		HashSet<?> pendingTickListEntriesHashSet = new HashSet<>();
		
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
		
		// TODO Make this available
		Object entityIdMap = IntHashMap.newInstance();
		f_entityIdMap.set(worldServer, entityIdMap);
		
		// TODO Make this available
		ArrayList<Object> loadedTileEntities = new ArrayList<Object>();
		ArrayList<Object> loadedEntities = new ArrayList<Object>();
		
		f_isClient.setBoolean(worldServer, false);
		f_worldAccesses.set(worldServer, new ArrayList<>()); // No one's listening, go home worldAccesses
		f_loadedEntityList.set(worldServer, loadedEntities);
		f_unloadedEntityList.set(worldServer, new ArrayList<>());
		f_loadedTileEntities.set(worldServer, loadedTileEntities);
		f_toLoadTileEntities.set(worldServer, new ArrayList<>());
		f_toUnloadTileEntities.set(worldServer, new ArrayList<>());
		f_playerEntities.set(worldServer, new ArrayList<>());
		f_weatherEffects.set(worldServer, new ArrayList<>());
		
		// TODO might want to play with this
		f_rand.set(worldServer, new Random());
		
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
		
		Log.i("Ticking updates");
	}
	
	public void  tickEntities(WorldInstance world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
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
	
	public Object getBlock(WorldInstance world, int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object block = m_getBlock.invoke(world.getWorld(), x, y, z);
		
		return block;
	}
	
	public int getBlockMetaData(WorldInstance world, int x, int y, int z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		int metaData = (int) m_getBlockMetadata.invoke(world.getWorld(), x, y, z);
		
		return metaData;
	}
	
	public boolean setBlock(WorldInstance world, int x, int y, int z, Object block, int metaData, boolean update, boolean sendChange) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		int flags = 4 | (sendChange ? 2 : 0) | (update ? 1 : 0);
		
		boolean succes = (boolean) m_setBlock.invoke(world.getWorld(), x, y, z, block, metaData, flags);
		
		if (succes)
			Log.i("Set block");
		else
			Log.w("Unsuccesfully set block");
		
		return succes;
	}
	
	
	/**
	 * Returns World, not WorldServer
	 */
	@Override
	public Class<?> getReflClass() {

		return World;
	}
	
	/**
	 * Returns the hashset containing NextTickListEntry objects
	 * @return
	 */
	public Object getPendingTicks(WorldInstance world) {
		
		return world.getPendingTickListEntries();
	}
	
	public ArrayList<Object> getLoadedTileEntities(WorldInstance world) throws IllegalArgumentException, IllegalAccessException {
		
		ArrayList<Object> tileEntities = (ArrayList<Object>) f_loadedTileEntities.get(world.getWorld());
		
		return tileEntities;
	}
	
	public void addTickEntry(WorldInstance world, int x, int y, int z, Object block, int scheduledTime, int priority) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		m_addTickEntry.invoke(world.getWorld(), x, y, z, block, scheduledTime, priority);
	}
	
	public void clearTickEntries(WorldInstance world) {
		world.clearPendingTickLists();
	}
}
