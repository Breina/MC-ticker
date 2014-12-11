package sim.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldInstance {

	private Object world;
	private Object player;
	// Where all future updates are stored as PendingTickListEntry objects
	private Set<Object> pendingTickListEntries, pendingTickListHashSet;
	private List<Object> loadedTileEntities, loadedEntities;
	private long worldTime;

	 // TODO make this so time is only updated when time changes, doesn't need to setTime when ticking
	private boolean doTimeUpdate;
	
	private int xSize, ySize, zSize;
	
	public WorldInstance() {
		worldTime = 0;
	}

	public Object getWorld() {
		return world;
	}

	public void setWorld(Object world) {
		this.world = world;
	}

	public Object getPlayer() {
		return player;
	}

	public void setPlayer(Object player) {
		this.player = player;
	}
	
	public void clearLoadedTileEntities() {
		loadedTileEntities.clear();
	}
	
	public void clearLoadedEntities() {
		loadedEntities.clear();
	}
	
	public void clearPendingTickLists() {
		pendingTickListEntries.clear();
		pendingTickListHashSet.clear();
	}

	public Set<Object> getPendingTickListEntries() {
		return pendingTickListEntries;
	}
	
	public Set<Object> getPendingTickListHashSet() {
		return pendingTickListHashSet;
	}

	public void setPendingTickListEntries(Set<Object> pendingTickListEntries) {
		this.pendingTickListEntries = pendingTickListEntries;
	}
	
	public void setPendingTickListHashSet(Set<Object> pendingTickListHashSet) {
		this.pendingTickListHashSet = pendingTickListHashSet;
	}

	public List<Object> getLoadedTileEntities() {
		return loadedTileEntities;
	}

	public void setLoadedTileEntities(ArrayList<Object> loadedTileEntities) {
		this.loadedTileEntities = loadedTileEntities;
	}

	public List<Object> getLoadedEntities() {
		return loadedEntities;
	}

	public void setLoadedEntities(ArrayList<Object> loadedEntities) {
		this.loadedEntities = loadedEntities;
	}

	public long getWorldTime() {
		return worldTime;
	}

	public void setWorldTime(long worldTime) {
		this.worldTime = worldTime;
	}

	public boolean doTimeUpdate() {
		return doTimeUpdate;
	}

	public void setDoTimeUpdate(boolean doTimeUpdate) {
		this.doTimeUpdate = doTimeUpdate;
	}

	public int getxSize() {
		return xSize;
	}

	public void setxSize(int xSize) {
		this.xSize = xSize;
	}

	public int getySize() {
		return ySize;
	}

	public void setySize(int ySize) {
		this.ySize = ySize;
	}

	public int getzSize() {
		return zSize;
	}

	public void setzSize(int zSize) {
		this.zSize = zSize;
	}

}
