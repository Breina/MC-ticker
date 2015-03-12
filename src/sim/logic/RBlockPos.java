package sim.logic;

import logging.Log;
import sim.loading.Linker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

class RBlockPos {

    private Field f_x, f_y, f_z;
	private Constructor<?> c_MutableBlockPos;

	public RBlockPos(Linker linker) throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		
		prepareBlockPos(linker);
		
		Log.i("Preparing BlockPos");
	}
	
	private void prepareBlockPos(Linker linker) throws NoSuchMethodException, SecurityException, NoSuchFieldException {

        Class<?> vec3i = linker.getClass("Vec3i");
        Class<?> mutableBlockPos = linker.getClass("BlockPos$MutableBlockPos");
		
		f_x = linker.field("x", vec3i);
		f_y = linker.field("y", vec3i);
		f_z = linker.field("z", vec3i);
		
		c_MutableBlockPos = mutableBlockPos.getDeclaredConstructor(int.class, int.class, int.class);
		c_MutableBlockPos.setAccessible(true);
	}
	
	public Object createInstance(int x, int y, int z) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		return c_MutableBlockPos.newInstance(x, y, z);
	}
	
	public int getX(Object instance) throws IllegalAccessException, IllegalArgumentException {
		return f_x.getInt(instance);
	}
	
	public int getY(Object instance) throws IllegalAccessException, IllegalArgumentException {
		return f_y.getInt(instance);
	}
	
	public int getZ(Object instance) throws IllegalAccessException, IllegalArgumentException {
		return f_z.getInt(instance);
	}
}
