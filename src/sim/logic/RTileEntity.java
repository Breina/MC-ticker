package sim.logic;

import logging.Log;
import sim.constants.Constants;
import sim.loading.Linker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class RTileEntity {

    private Method m_createAndLoadEntity, m_writeToNBT;


	public RTileEntity(Linker linker) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, NoSuchFieldException {

		prepareTileEntity(linker);

		Log.i("Preparing tile entities");
	}

	private void prepareTileEntity(Linker linker) throws NoSuchMethodException, SecurityException {

        Class<?> tileEntity = linker.getClass("TileEntity");
		Class<?> NBTTagCompound = linker.getClass("NBTTagCompound");

		m_createAndLoadEntity = linker.method("createAndLoadEntity", tileEntity, NBTTagCompound);
		m_writeToNBT = tileEntity.getMethod(Constants.TILEENTITY_READFROMNBT, NBTTagCompound);
	}

	public Object createTileEntityFromNBT(Object nbtTagCompound) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        return m_createAndLoadEntity.invoke(null, nbtTagCompound);
	}

	public void getNBTFromTileEntity(Object tileEntity, Object mcTag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		m_writeToNBT.invoke(tileEntity, mcTag);

	}
}