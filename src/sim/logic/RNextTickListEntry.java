package sim.logic;

import java.lang.reflect.Field;

import sim.constants.Constants;
import sim.loading.ClassTester;
import sim.loading.Linker;
import logging.Log;

public class RNextTickListEntry {
	
	private Class<?> NextTickListEntry;
	private Field f_blockPos, f_scheduledTime, f_priority, f_block;
	
	public RNextTickListEntry(Linker linker) throws NoSuchFieldException, SecurityException {
		
		prepareNextTickListEntry(linker);
		
		Log.i("Preparing Ticks");
	}
	
	public void prepareNextTickListEntry(Linker linker) throws NoSuchFieldException, SecurityException {
		
		NextTickListEntry = linker.getClass("NextTickListEntry");
		
		f_scheduledTime = linker.field("scheduledTime", NextTickListEntry);
		
		f_block = NextTickListEntry.getDeclaredField(Constants.NEXTTICKLISTENTRY_BLOCK);
		f_block.setAccessible(true);
		
		f_blockPos = NextTickListEntry.getDeclaredField(Constants.NEXTTICKLISTENTRY_BLOCKPOS);
		f_blockPos.setAccessible(true);
		
		f_priority = NextTickListEntry.getDeclaredField(Constants.NEXTTICKLISTENTRY_PRIORITY);
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
