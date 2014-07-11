package sim.logic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import logging.Log;
import sim.constants.Constants;
import sim.loading.ClassTester;
import sim.loading.Linker;


public class RBootstrap implements ISimulated {
	
	private Class<?> Bootstrap;
	
//	private Class<?> Item;
//	private Method m_getIconString, m_getItemById;
	
	public RBootstrap(Linker linker) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		prepareBootstrap(linker);
	
		Log.i("Bootstrapping...");
	}
	
	private void prepareBootstrap(Linker linker) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		Bootstrap = linker.getClass("Bootstrap");
		Method m_register = Bootstrap.getDeclaredMethod(Constants.BOOTLOADER_REGISTER);
		m_register.invoke(null);
	}

	@Override
	public Class<?> getReflClass() {
		return Bootstrap;
	}
	
//	private void prepareItem(Linker linker) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		
//		Item = linker.getClass("Item");
//		
//		m_getIconString = linker.method("getIconString", Item);
//		m_getItemById = linker.method("getItemById", Item, int.class);
//		
//		Method m_registerItems = linker.method("registerItems", Item);
//		m_registerItems.invoke(Item);
//	}
//	
//	public String getItemString(Object item) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		
//		String string = (String) m_getIconString.invoke(item);
//		
//		return string;
//	}
//	
//	public Object getItemById(int id) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		
//		Object item = m_getItemById.invoke(null, id);
//		
//		return item;
//	}
//
//	@Override
//	public Class<?> getReflClass() {
//		return Item;
//	}

}
