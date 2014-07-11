package sim.logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import logging.Log;
import sim.loading.Linker;

public class RTileEntity implements ISimulated {
	
	private Class<?> TileEntity;
	private Method m_createAndLoadEntity, m_writeToNBT;
	
	// DEBUG
	private Method m_getInventoryName, m_getUnlocalizedName, m_getSizeInventory, m_getStackInSlot, m_loadItemStackFromNBT, m_readFromNBT;
	private Constructor<?> c_ItemStack;
	
	
	public RTileEntity(Linker linker, Class<?> NBTTagCompoundClass) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		
		prepareTileEntity(linker, NBTTagCompoundClass);
		
		Log.i("Preparing tile entities");		
	}
	
	private void prepareTileEntity(Linker linker, Class<?> NBTTagCompound) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		
		TileEntity = linker.getClass("TileEntity");
		Class<?> IInventory = linker.getClass("IInventory");
		Class<?> ItemStack = linker.getClass("ItemStack");
		
		m_createAndLoadEntity = linker.method("createAndLoadEntity", TileEntity, NBTTagCompound);
		
		// TODO: Can't use linker yet
		m_writeToNBT = TileEntity.getMethod("b", NBTTagCompound);
		
		// DEBUG
		m_getInventoryName = linker.method("getInventoryName", IInventory);
		m_getUnlocalizedName = linker.method("getUnlocalizedName", ItemStack);
		m_getSizeInventory = linker.method("getSizeInventory", IInventory);
		m_getStackInSlot = linker.method("getStackInSlot", IInventory, int.class);
		m_loadItemStackFromNBT = linker.method("loadItemStackFromNBT", ItemStack, NBTTagCompound);
		m_readFromNBT = linker.method("readFromNBT", ItemStack, NBTTagCompound);
		
		c_ItemStack = ItemStack.getDeclaredConstructor();
		c_ItemStack.setAccessible(true);
	}
	
	public Object createTileEntityFromNBT(Object nbtTagCompound) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object tileEntity = m_createAndLoadEntity.invoke(null, nbtTagCompound);
		
		return tileEntity;
	}
	
	public void debug(Object tileEntity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		System.out.println("Inventory: " + m_getInventoryName.invoke(tileEntity));
		
		int size = (int) m_getSizeInventory.invoke(tileEntity);
		
		System.out.println("Size:      " + size);
		
		for (int i = 0; i < size; i++) {
			
			Object itemStack = m_getStackInSlot.invoke(tileEntity, i);
			
			if (itemStack != null)
				System.out.println(" - " + m_getUnlocalizedName.invoke(itemStack));			
		}
	}
	
	public void getNBTFromTileEntity(Object tileEntity, Object mcTag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		m_writeToNBT.invoke(tileEntity, mcTag);
		
	}
	
	public Object loadItemStackFromNBT(Object tag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		return m_loadItemStackFromNBT.invoke(null, tag);
	}
	
	public Object readFromNBT(Object tag) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object itemStack = c_ItemStack.newInstance();
		
		return m_readFromNBT.invoke(itemStack, tag);
	}

	@Override
	public Class<?> getReflClass() {
		return TileEntity;
	}
}
