package sim.logic;

//import com.mojang.authlib.GameProfile;
import logging.Log;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import sim.constants.Constants;
import sim.loading.Linker;
import sim.objects.WorldInstance;

import java.lang.reflect.*;
import java.util.*;

/**
 * This class is an intermediate between the Simulator's high level logic and all of World's reflection
 */
public class RWorld {
	
	private Class<?> WorldServer, WorldProvider, WorldType, WorldSettings, WorldInfo, IChunkProvider, GameType, World,
			IntHashMap, BlockPos, WorldBorder, ServerBlockEventList;

	private Method m_tickUpdates, m_setWorldTime, m_getWorldTime, m_getProviderForDimension,
			m_spawnEntityInWorld, m_addTickEntry, m_getBlockState, m_setBlockState,
			m_incrementTotalWorldTime, m_getTileEntity, m_update, m_getEventID,
			m_getEventParameter, m_getEventPos;

	private Field f_provider, f_levelSaving, f_theProfiler, f_pendingTickListEntriesTreeSet, f_chunkProvider,
			f_isRemote, f_worldAccesses, f_loadedEntityList, f_unloadedEntityList, f_playerEntities, f_weatherEffects,
			f_entitiesById, f_entitiesByUuid, f_rand, f_pendingTickListEntriesHashSet, f_pendingTickListEntriesThisTick,
			f_worldInfo, f_worldBorder, f_lightUpdateBlockList, f_tickableTileEntities, f_loadedTileEntityList,
			f_addedTileEntityList, f_tileEntitiesToBeRemoved, f_serverBlockEvents;

	private Constructor<?> c_worldType, c_worldSettings, c_worldInfo, c_entityOtherPlayerMP, c_worldBorder,
			c_serverBlockEvents, c_gameProfile;

	private Enum<?> e_GameType;

	private RBlock rBlock;
	private RBlockPos rBlockPos;
	private RIntHashMap rIntHashMap;
	private REntity rEntity;
	
	public RWorld(Linker linker, Object profiler, RBlock rBlock, RBlockPos rBlockPos, RIntHashMap rIntHashMap, REntity rEntity) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		
		prepareWorld(linker, profiler);

		this.rBlock = rBlock;
		this.rBlockPos = rBlockPos;
		this.rIntHashMap = rIntHashMap;
		this.rEntity = rEntity;
		
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
		WorldBorder							= linker.getClass("WorldBorder");
		IChunkProvider						= linker.getClass("IChunkProvider");
		IntHashMap							= linker.getClass("IntHashMap");
		BlockPos							= linker.getClass("BlockPos");
		ServerBlockEventList				= linker.getClass("WorldServer$ServerBlockEventList");

		Class<?> IBlockState				= linker.getClass("IBlockState");
		Class<?> Block						= linker.getClass("Block");
		Class<?> EntityOtherPlayerMP		= linker.getClass("EntityOtherPlayerMP");
		Class<?> IUpdatePlayerListBox		= linker.getClass("IUpdatePlayerListBox");
		Class<?> BlockEventData				= linker.getClass("BlockEventData");
        Class<?> GameProfile                = linker.getClass("GameProfile");

		GameType							= linker.getClass("WorldSettings$GameType");
		
		f_provider							= linker.field("provider", World);
		
		f_levelSaving						= linker.field("disableLevelSaving", WorldServer);
		
		f_pendingTickListEntriesTreeSet		= linker.field("pendingTickListEntriesTreeSet", WorldServer);
		f_pendingTickListEntriesHashSet		= linker.field("pendingTickListEntriesHashSet", WorldServer);
		f_pendingTickListEntriesThisTick	= linker.field("pendingTickListEntriesThisTick", WorldServer);
		
		f_entitiesById						= linker.field("entitiesById", World);
		f_entitiesByUuid					= linker.field("entitiesByUuid", WorldServer);
		
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
		f_worldBorder						= linker.field("worldBorder", World);

		
		c_worldType							= WorldType.getDeclaredConstructor(int.class, String.class);
		c_worldType							.setAccessible(true);
		c_worldSettings						= WorldSettings.getConstructor(long.class, GameType, boolean.class, boolean.class,
				WorldType);
		c_worldInfo							= WorldInfo.getConstructor(WorldSettings, String.class);
		c_worldBorder						= WorldBorder.getDeclaredConstructor();

        c_serverBlockEvents					= ServerBlockEventList.getDeclaredConstructor();
        c_serverBlockEvents					.setAccessible(true);

        c_gameProfile                       = GameProfile.getDeclaredConstructor(UUID.class, String.class);
        c_entityOtherPlayerMP				= EntityOtherPlayerMP.getDeclaredConstructor(World, GameProfile);

		m_getProviderForDimension			= linker.method("getProviderForDimension", WorldProvider, int.class);
        m_tickUpdates						= linker.method("tickUpdates", WorldServer, boolean.class);
		
		m_setWorldTime 						= linker.method("setWorldTime", World, long.class );
		m_incrementTotalWorldTime			= linker.method("incrementTotalWorldTime", WorldInfo, long.class);

		m_getWorldTime						= linker.method("getTotalWorldTime", World);
		m_spawnEntityInWorld				= linker.method("spawnEntityInWorld", World, linker.getClass("Entity"));
		m_getTileEntity						= linker.method("getTileEntity", World, BlockPos);
		m_update							= linker.method("update", IUpdatePlayerListBox);

		m_getEventID						= linker.method("getEventID", BlockEventData);
		m_getEventParameter					= linker.method("getEventParameter", BlockEventData);

        // TODO can't use linker yet for these
		m_getBlockState						= World.getDeclaredMethod(Constants.WORLD_GETBLOCKSTATE, BlockPos);
		m_setBlockState						= World.getDeclaredMethod(Constants.WORLD_SETBLOCKSTATE, BlockPos, IBlockState, int.class);
		m_addTickEntry						= World.getDeclaredMethod(Constants.WORLD_ADDTICKENTRY, BlockPos, Block, int.class, int.class);
		m_getEventPos						= BlockEventData.getDeclaredMethod(Constants.BLOCKEVENTDATA_GETBLOCKPOS);

		f_theProfiler						= World.getField(Constants.WORLD_THEPROFILER);
		f_theProfiler						.setAccessible(true);

		f_serverBlockEvents					= WorldServer.getDeclaredField(Constants.WORLDSERVER_SERVERBLOCKEVENTLIST);
		f_serverBlockEvents					.setAccessible(true);
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
		f_pendingTickListEntriesThisTick.set(worldServer, new ArrayList<>());
		
		Object worldType = c_worldType.newInstance(_worldTypeId, _worldType);
		e_GameType = Enum.valueOf((Class<Enum>) GameType, _gameType);
		
		Object worldSettings = c_worldSettings.newInstance(_seed, e_GameType, _mapFeaturesEnabled, _hardcoreEnabled, worldType);
		
		Object worldInfo = c_worldInfo.newInstance(worldSettings, Constants.APPNAME);
		f_worldInfo.set(worldServer, worldInfo);

		f_worldBorder.set(worldServer, c_worldBorder.newInstance());

		Object chunkProvider = Proxy.newProxyInstance(IChunkProvider.getClassLoader(),
				new Class[]{IChunkProvider}, rChunkProvider);		
		
		f_chunkProvider.set(worldServer, chunkProvider);
		
		f_isRemote.setBoolean(worldServer, false);

		Object blockEventArray = Array.newInstance(ServerBlockEventList, 2);
		Array.set(blockEventArray, 0, c_serverBlockEvents.newInstance());
        Array.set(blockEventArray, 1, c_serverBlockEvents.newInstance());
		f_serverBlockEvents.set(worldServer, blockEventArray);

		ArrayList<Object> loadedTileEntities = new ArrayList<>();
		ArrayList<Object> tickableTileEntities = new ArrayList<>();
		ArrayList<Object> loadedEntities = new ArrayList<>();
		Object entitiesById = IntHashMap.newInstance();
		HashMap entitiesByUuid = new HashMap();
		
		f_worldAccesses.set(worldServer, new ArrayList<>()); // No one's listening, go home worldAccesses
		f_loadedEntityList.set(worldServer, loadedEntities);
		f_entitiesById.set(worldServer, entitiesById);
		f_entitiesByUuid.set(worldServer, entitiesByUuid);
		f_unloadedEntityList.set(worldServer, new ArrayList<>());
		f_loadedTileEntityList.set(worldServer, loadedTileEntities);
		f_tickableTileEntities.set(worldServer, tickableTileEntities);
		f_addedTileEntityList.set(worldServer, new ArrayList<>());
		f_tileEntitiesToBeRemoved.set(worldServer, new ArrayList<>());
		f_playerEntities.set(worldServer, new ArrayList<>());
		f_weatherEffects.set(worldServer, new ArrayList<>());

		// TODO might want to play with this
		f_rand.set(worldServer, new Random());
		
		f_lightUpdateBlockList.set(worldServer, new int[32768]);

		Object gameProfile = c_gameProfile.newInstance(
                UUID.fromString("4865726f-6272-696e-6520-3d207265616c"), Constants.PLAYERNAME);
		Object entityPlayer = c_entityOtherPlayerMP.newInstance(worldServer, gameProfile);

		rChunkProvider.setEmptyChunk(rChunk.generateEmptyChunk(worldServer));
		
		WorldInstance world = new WorldInstance(rIntHashMap);
			world.setWorld(worldServer);
			world.setLoadedTileEntities(loadedTileEntities);
			world.setTickableTileEntities(tickableTileEntities);
			world.setLoadedEntities(loadedEntities);
			world.setEntitiesById(entitiesById);
			world.setEntitiesByUuid(entitiesByUuid);
			world.setPendingTickListEntries(pendingTickListEntriesTreeSet);
			world.setPendingTickListHashSet(pendingTickListEntriesHashSet);
			world.setDoTimeUpdate(true);
            world.setBlockEventCacheIndex(0);
			world.setPlayer(entityPlayer);
		return world;
	}
	
	/**
	 * This invokes the relevant function once
	 */
	public boolean tick(WorldInstance world, long advanceTicks) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		if (Constants.DEBUG_WORLD)
			System.out.println("Ticking " + world.getPendingTickListEntries().size() + " pending updates...");
		
		advanceTicks(world, advanceTicks);

		// The order of these is cast in stone
        boolean moreUpdatesExist = tickUpdates(world);
        tickBlockEvents(world);
        tickEntities(world);
        tickTileEntities(world);

		world.setDoTimeUpdate(true);

		return moreUpdatesExist;
	}

    public boolean tickUpdates(WorldInstance world) throws InvocationTargetException, IllegalAccessException {
        return (boolean) m_tickUpdates.invoke(world.getWorld(), false);
    }

	public void tickTileEntities(WorldInstance world) throws InvocationTargetException, IllegalAccessException {

		// TODO I would prefer using an iterator, but it throws a ConcurrentModificationException
		Object[] tileEntities = world.getTickableTileEntities().toArray();

		for (int i = 0; i < tileEntities.length; i++)
			m_update.invoke(tileEntities[i]);
	}

	public void tickEntities(WorldInstance world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Iterator<Object> entityIterator = world.getLoadedEntities().iterator();

		while (entityIterator.hasNext()) {
			Object entity = entityIterator.next();

			if (rEntity.isDead(entity)) {
				entityIterator.remove();
				continue;
			}

			rEntity.update(entity);
		}
	}

	/**
	 * WorldServer overrides World's behavior without calling its super method,
	 * so fixing that here.
	 */
	public void tickBlockEvents(WorldInstance world) throws IllegalAccessException, InvocationTargetException, InstantiationException {

		Object blockEvents = f_serverBlockEvents.get(world.getWorld());

        int index = world.getBlockEventCacheIndex();
        Object blockEventDataArray = Array.get(blockEvents, index);
        index ^= 1;
        world.setBlockEventCacheIndex(index);

        // Repeat until there are no more block events (meaning instantwire)
        while (!((ArrayList) blockEventDataArray).isEmpty()) {

            Object[] blockEventDataObjects = ((ArrayList) blockEventDataArray).toArray();
            ((ArrayList) blockEventDataArray).clear();

            for (int j = 0; j < blockEventDataObjects.length; j++) {

                Object blockEventData = blockEventDataObjects[j];

                int eventId = (int) m_getEventID.invoke(blockEventData);
                int eventParameter = (int) m_getEventParameter.invoke(blockEventData);
                Object blockPos = m_getEventPos.invoke(blockEventData);

                Object blockState = getBlockState(world, rBlockPos.getX(blockPos), rBlockPos.getY(blockPos), rBlockPos.getZ(blockPos));
                Object block = rBlock.getBlockFromState(blockState);

                Object worldBlock = rBlock.getBlockFromState(getBlockState(world, blockPos));

                if (block.equals(worldBlock))
                    rBlock.onBlockEventReceived(block, world.getWorld(), blockPos, blockState, eventId, eventParameter);
                else
                    Log.w("Ignored block event at " + blockPos);
            }

            ((ArrayList) blockEventDataArray).clear();
        }
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

		Object worldInfo = f_worldInfo.get(world.getWorld());
		m_incrementTotalWorldTime.invoke(worldInfo, time);
		
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
		
		return getBlockState(world, rBlockPos.createInstance(x, y, z));
	}

    private Object getBlockState(WorldInstance world, Object blockPos) throws InvocationTargetException, IllegalAccessException {

        Object blockState = m_getBlockState.invoke(world.getWorld(), blockPos);

        return blockState;
    }
	
	public boolean setBlockState(WorldInstance world, int x, int y, int z, Object blockState, boolean update, boolean sendChange) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		int flags = 4 | (sendChange ? 2 : 0) | (update ? 1 : 0);
		
		boolean succes = (boolean) m_setBlockState.invoke(world.getWorld(), rBlockPos.createInstance(x, y, z), blockState, flags);
		
		if (!succes)
			Log.w("Set block: no changes");
		
		return succes;
	}
	
	public List<Object> getLoadedTileEntities(WorldInstance world) throws IllegalArgumentException, IllegalAccessException {
		
		List<Object> tileEntities = (List<Object>) f_loadedTileEntityList.get(world.getWorld());
		
		return tileEntities;
	}

	public Object getTileEntity(WorldInstance world, Object blockPos) throws InvocationTargetException, IllegalAccessException {
		return m_getTileEntity.invoke(world.getWorld(), blockPos);
	}
	
	public void addTickEntry(WorldInstance world, int x, int y, int z, Object block, int scheduledTime, int priority) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		m_addTickEntry.invoke(world.getWorld(), rBlockPos.createInstance(x, y, z), block, scheduledTime, priority);
	}
}
