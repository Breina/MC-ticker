package sim.logic;

import logging.Log;
import sim.constants.Constants;
import sim.loading.Linker;

import java.lang.reflect.Field;

class RNextTickListEntry {

    private Field f_blockPos, f_scheduledTime, f_priority, f_block;
	
	public RNextTickListEntry(Linker linker) throws NoSuchFieldException, SecurityException {
		
		prepareNextTickListEntry(linker);
		
		Log.i("Preparing Ticks");
	}
	
	void prepareNextTickListEntry(Linker linker) throws NoSuchFieldException, SecurityException {

        Class<?> nextTickListEntry = linker.getClass("NextTickListEntry");
		
		f_scheduledTime = linker.field("scheduledTime", nextTickListEntry);
		
		f_block = nextTickListEntry.getDeclaredField(Constants.NEXTTICKLISTENTRY_BLOCK);
		f_block.setAccessible(true);
		
		f_blockPos = nextTickListEntry.getDeclaredField(Constants.NEXTTICKLISTENTRY_BLOCKPOS);
		f_blockPos.setAccessible(true);
		
		f_priority = nextTickListEntry.getDeclaredField(Constants.NEXTTICKLISTENTRY_PRIORITY);
	}
	
	public Object getBlockPos(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return f_blockPos.get(instance);
	}
	
	public long getScheduledTime(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return f_scheduledTime.getLong(instance);
	}
	
	public int getPriority(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return f_priority.getInt(instance);
	}
	
	public Object getBlock(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return f_block.get(instance);
	}
}
