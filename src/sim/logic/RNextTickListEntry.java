package sim.logic;

import java.lang.reflect.Field;

import sim.constants.Constants;
import sim.loading.ClassTester;
import sim.loading.Linker;
import logging.Log;

public class RNextTickListEntry implements ISimulated {
	
	private Class<?> NextTickListEntry;
	private Field f_xCoord, f_yCoord, f_zCoord, f_scheduledTime, f_priority, f_block;
	
	public RNextTickListEntry(Linker linker) throws NoSuchFieldException, SecurityException {
		
		prepareNextTickListEntry(linker);
		
		Log.i("Preparing Ticks");
	}
	
	public void prepareNextTickListEntry(Linker linker) throws NoSuchFieldException, SecurityException {
		
		NextTickListEntry = linker.getClass("NextTickListEntry");
		
		f_xCoord = linker.field("xCoord", NextTickListEntry);
		f_yCoord = linker.field("yCoord", NextTickListEntry);
		f_zCoord = linker.field("zCoord", NextTickListEntry);
		f_scheduledTime = linker.field("scheduledTime", NextTickListEntry);
		f_priority = linker.field("priority", NextTickListEntry);
		
		f_block = NextTickListEntry.getDeclaredField(Constants.NEXTTICKLISTENTRY_BLOCK);
		f_block.setAccessible(true);
	}
	
	public int getXCoord(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return f_xCoord.getInt(instance);
	}
	
	public int getYCoord(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return f_yCoord.getInt(instance);
	}
	
	public int getZCoord(Object instance) throws IllegalArgumentException, IllegalAccessException {
		return f_zCoord.getInt(instance);
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
	
	@Override
	public Class<?> getReflClass() {
		
		return NextTickListEntry;
	}
}
