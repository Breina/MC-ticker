package sim.objects;

import presentation.objects.Block;

import java.util.*;

@Deprecated
public class WorldState {
	
	private long worldTime;
	private Block[][][] blocks;
	private List<Object> tileEntities, tickableTileEntities, entities;
	private Set<Object> tileTicks, tileTickHashes;
	private Object entitiesById; // IntHashMap
	private HashMap entitiesByUuid;
	
	public WorldState(long worldTime, Block[][][] blocks, List<Object> tileEntities,
					  List<Object> tickableTileEntities, List<Object> entities,
					  Object entitiesById, HashMap entitiesByUuid, Set<Object> tileTicks,
					  Set<Object> tileTickHashes) {

		// TODO why create a new list for some?
		this.worldTime = worldTime;
		this.blocks = blocks;
		this.tileEntities = new ArrayList<>(tileEntities);
		this.tickableTileEntities = tickableTileEntities;
		this.entities = new ArrayList<>(entities);
		this.entitiesById = entitiesById;
		this.entitiesByUuid = entitiesByUuid;
		this.tileTicks = new TreeSet<>(tileTicks);
		this.tileTickHashes = new HashSet<>(tileTickHashes);
	}
	
	public long getWorldTime() {
		return worldTime;
	}

	public Block[][][] getBlocks() {
		return blocks;
	}

	public List<Object> getTileEntities() {
		return tileEntities;
	}

	public List<Object> getTickableEntities() {
		return tickableTileEntities;
	}

	public List<Object> getEntities() {
		return entities;
	}

	public Object getEntitiesById() {
		return entitiesById;
	}

	public HashMap getEntitiesByUuid() {
		return entitiesByUuid;
	}

	public Collection<?> getTileTicks() {
		return tileTicks;
	}
	
	public Set<Object> getTileTickHashes() {
		return tileTickHashes;
	}
	
	@Override
	public String toString() {
		return "blocks: " + blocks.length + ", tileEntities: " + tileEntities.size() + ", entities: " + entities.size() +
				", tileTicks: " + tileTicks.size();
	}
}
