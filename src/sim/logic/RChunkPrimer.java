package sim.logic;

import logging.Log;
import sim.constants.Constants;
import sim.loading.Linker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

class RChunkPrimer {

    private Constructor<?> c_chunkPrimer;
	private Field f_data;
	
	public RChunkPrimer(Linker linker) throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		
		prepareChunkPrimer(linker);
		
		Log.i("Preparing ChunkPrimers");
	}
	
	private void prepareChunkPrimer(Linker linker) throws NoSuchMethodException, SecurityException, NoSuchFieldException {

        Class<?> chunkPrimer = linker.getClass("ChunkPrimer");
		
		c_chunkPrimer = chunkPrimer.getDeclaredConstructor();
		
		f_data = chunkPrimer.getDeclaredField(Constants.CHUNKPRIMER_DATA);
		f_data.setAccessible(true);
	}
	
	public Object createChunkPrimer() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		return c_chunkPrimer.newInstance();
	}
	
	public void setData(Object chunkPrimer, short[] data) throws IllegalArgumentException, IllegalAccessException {
		
		if (data.length != 65536)
			throw new RuntimeException("The data array passed to chunkProvider should be 65536 long, but is " + data.length);
		
		f_data.set(chunkPrimer, data);
	}
}
