package sim.logic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import logging.Log;
import sim.loading.Linker;

public class REntity implements ISimulated {
	
	private Class<?> Entity, EntityList;
	private Method m_writeToNBT, m_createEntityFromNBT;
	
	public REntity(Linker linker, Class<?> NBTTagCompoundClass, Class<?> World) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException {
		
		prepareEntity(linker, NBTTagCompoundClass, World);
		
		Log.i("Preparing entities");		
	}
	
	private void prepareEntity(Linker linker, Class<?> NBTTagCompound, Class<?> World) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException {
		
		Entity = linker.getClass("Entity");
		EntityList = linker.getClass("EntityList");
		
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

	@Override
	public Class<?> getReflClass() {
		return Entity;
	}
}
