package sim.objects;

import java.util.*;

public class WorldState {
	
	private long worldTime;
	private byte[] ids, data;
	private List<Object> tileEntities, tickableTileEntities, entities;
	private Set<Object> tileTicks, tileTickHashes;
	
	public WorldState(long worldTime, byte[] ids, byte[] data, List<Object> tileEntities,
					  List<Object> tickableTileEntities, List<Object> entities, Set<Object> tileTicks,
					  Set<Object> tileTickHashes) {
		
		this.worldTime = worldTime;
		this.ids = ids;
		this.data = data;
		this.tileEntities = new ArrayList<>(tileEntities);
		this.tickableTileEntities = tickableTileEntities;
		this.entities = new ArrayList<>(entities);
		this.tileTicks = new TreeSet<>(tileTicks);
		this.tileTickHashes = new HashSet<>(tileTickHashes);
	}
	
	public long getWorldTime() {
		return worldTime;
	}

	public byte[] getIds() {
		return ids;
	}

	public byte[] getData() {
		return data;
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

	public Collection<?> getTileTicks() {
		return tileTicks;
	}
	
	public Set<Object> getTileTickHashes() {
		return tileTickHashes;
	}
	
	@Override
	public String toString() {
		return "ids: " + ids.length + ", data: " + data.length + ", tileEntities: " + tileEntities.size() + ", entities: " + entities.size() +
				", tileTicks: " + tileTicks.size();
	}
}
