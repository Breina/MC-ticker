package sim.logic;

import logging.Log;
import sim.loading.Linker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


class RBootstrap {

    private Method m_register;
	
//	private Class<?> Item;
//	private Method m_getIconString, m_getItemById;
	
	public RBootstrap(Linker linker) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		prepareBootstrap(linker);
	
		Log.i("Bootstrapping...");
	}
	
	private void prepareBootstrap(Linker linker) throws IllegalArgumentException, NoSuchMethodException, SecurityException {

        Class<?> bootstrap = linker.getClass("Bootstrap");
		
		m_register = linker.method("register", bootstrap);
	}
	
	public void register() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_register.invoke(null);
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
