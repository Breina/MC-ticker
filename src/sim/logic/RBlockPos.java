package sim.logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import logging.Log;

import sim.loading.Linker;

public class RBlockPos {
	
	private Class<?> MutableBlockPos;
	private Method m_getX, m_getY, m_getZ;
	private Constructor<?> c_MutableBlockPos;

	public RBlockPos(Linker linker) throws NoSuchMethodException, SecurityException {
		
		prepareBlockPos(linker);
		
		Log.i("Preparing BlockPos");
	}
	
	private void prepareBlockPos(Linker linker) throws NoSuchMethodException, SecurityException {
		
		MutableBlockPos = linker.getClass("BlockPos$MutableBlockPos");
		
		m_getX = linker.method("getX", MutableBlockPos);
		m_getY = linker.method("getY", MutableBlockPos);
		m_getZ = linker.method("getZ", MutableBlockPos);
		
		c_MutableBlockPos = MutableBlockPos.getDeclaredConstructor(int.class, int.class, int.class);
		c_MutableBlockPos.setAccessible(true);
	}
	
	public Object createInstance(int x, int y, int z) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		return c_MutableBlockPos.newInstance(x, y, z);
	}
	
	public int getX(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (int) m_getX.invoke(instance);
	}
	
	public int getY(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (int) m_getY.invoke(instance);
	}
	
	public int getZ(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (int) m_getZ.invoke(instance);
	}
}
