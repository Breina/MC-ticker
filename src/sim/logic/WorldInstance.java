package sim.logic;

import java.util.ArrayList;
import java.util.HashSet;

public class WorldInstance {

	private Object world;
	// Where all future updates are stored as PendingTickListEntry objects
	private HashSet<?> pendingTickListEntriesHashSet;
	private ArrayList<Object> loadedTileEntities, loadedEntities;
	private long worldTime;
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

	public HashSet<?> getPendingTickListEntriesHashSet() {
		return pendingTickListEntriesHashSet;
	}

	public void setPendingTickListEntriesHashSet(HashSet<?> pendingTickListEntriesHashSet) {
		this.pendingTickListEntriesHashSet = pendingTickListEntriesHashSet;
	}

	public ArrayList<Object> getLoadedTileEntities() {
		return loadedTileEntities;
	}

	public void setLoadedTileEntities(ArrayList<Object> loadedTileEntities) {
		this.loadedTileEntities = loadedTileEntities;
	}

	public ArrayList<Object> getLoadedEntities() {
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
