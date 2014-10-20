package sim.logic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import logging.Log;
import sim.loading.Linker;

public class REntity {
	
	private Class<?> Entity, EntityList;
	private Method m_writeToNBT, m_createEntityFromNBT;
	
	public REntity(Linker linker) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException {
		
		prepareEntity(linker);
		
		Log.i("Preparing entities");		
	}
	
	private void prepareEntity(Linker linker) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException {
		
		Entity = linker.getClass("Entity");
		EntityList = linker.getClass("EntityList");
		Class<?> NBTTagCompound = linker.getClass("NBTTagCompound");
		Class<?> World = linker.getClass("World"); // TODO check this
		
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
}
