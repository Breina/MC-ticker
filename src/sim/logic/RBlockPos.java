package sim.logic;

import logging.Log;
import sim.loading.Linker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class RBlockPos {
	
	private Class<?> MutableBlockPos, Vec3i;
	private Field f_x, f_y, f_z;
	private Constructor<?> c_MutableBlockPos;

	public RBlockPos(Linker linker) throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		
		prepareBlockPos(linker);
		
		Log.i("Preparing BlockPos");
	}
	
	private void prepareBlockPos(Linker linker) throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		
		Vec3i = linker.getClass("Vec3i");
		MutableBlockPos = linker.getClass("BlockPos$MutableBlockPos");
		
		f_x = linker.field("x", Vec3i);
		f_y = linker.field("y", Vec3i);
		f_z = linker.field("z", Vec3i);
		
		c_MutableBlockPos = MutableBlockPos.getDeclaredConstructor(int.class, int.class, int.class);
		c_MutableBlockPos.setAccessible(true);
	}
	
	public Object createInstance(int x, int y, int z) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		return c_MutableBlockPos.newInstance(x, y, z);
	}
	
	public int getX(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return f_x.getInt(instance);
	}
	
	public int getY(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return f_y.getInt(instance);
	}
	
	public int getZ(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return f_z.getInt(instance);
	}
}
