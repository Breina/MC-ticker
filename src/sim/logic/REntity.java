package sim.logic;

import logging.Log;
import sim.loading.Linker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class REntity {
	
	private Class<?> Entity, EntityList;
	private Method m_writeToNBT, m_createEntityFromNBT;
	private Field f_posX, f_posY, f_posZ, f_motionX, f_motionY, f_motionZ, f_width, f_height;
	
	public REntity(Linker linker) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		
		prepareEntity(linker);
		
		Log.i("Preparing entities");		
	}
	
	private void prepareEntity(Linker linker) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		
		Entity = linker.getClass("Entity");
		EntityList = linker.getClass("EntityList");
		Class<?> NBTTagCompound = linker.getClass("NBTTagCompound");
		Class<?> World = linker.getClass("World");

		f_posX		= linker.field("posX", Entity);
		f_posY		= linker.field("posY", Entity);
		f_posZ		= linker.field("posZ", Entity);
		f_motionX	= linker.field("motionX", Entity);
		f_motionY	= linker.field("motionY", Entity);
		f_motionZ	= linker.field("motionZ", Entity);
		f_width		= linker.field("width", Entity);
		f_height	= linker.field("height", Entity);

		m_writeToNBT = linker.method("writeToNBT", Entity, NBTTagCompound);
		m_createEntityFromNBT = linker.method("createEntityFromNBT", EntityList, NBTTagCompound, World);
	}
	
	public Object createEntityFromNBT(Object nbtTagCompound, Object world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object entity = m_createEntityFromNBT.invoke(null, nbtTagCompound, world);
		
		return entity;
	}
	
	public void getNBTFromEntity(Object entity, Object mcTag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_writeToNBT.invoke(entity, mcTag);
	}

	public double getX(Object entity) throws IllegalAccessException {
		return f_posX.getDouble(entity);
	}

	public double getY(Object entity) throws IllegalAccessException {
		return f_posY.getDouble(entity);
	}

	public double getZ(Object entity) throws IllegalAccessException {
		return f_posZ.getDouble(entity);
	}

	public double getMotionX(Object entity) throws IllegalAccessException {
		return f_motionX.getDouble(entity);
	}

	public double getMotionY(Object entity) throws IllegalAccessException {
		return f_motionY.getDouble(entity);
	}

	public double getMotionZ(Object entity) throws IllegalAccessException {
		return f_motionZ.getDouble(entity);
	}

	public float getWidth(Object entity) throws IllegalAccessException {
		return f_width.getFloat(entity);
	}

	public float getHeight(Object entity) throws IllegalAccessException {
		return f_height.getFloat(entity);
	}
}
