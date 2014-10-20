package sim.logic;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import logging.Log;
import sim.constants.Constants;
import sim.loading.ClassTester;
import sim.loading.Linker;
import utils.CircularByteBuffer;
import utils.Tag;

public class RNBTTags {
	
	private Class<?> NBTTagCompound, NBTTagList, NBTSizeTracker;
	private Constructor<?> c_NBTTagCompound, c_NBTSizeTracker;
	private Method m_load, m_write;
	
	private final static long MAXSIZE = 512l;
	
	// DEBUG
	private Method m_getTagList, m_getCompoundTagAt;

	public RNBTTags(Linker linker) throws NoSuchMethodException, SecurityException {
		
		prepareNBTTagCompound(linker);
		
		Log.i("Preparing NBT-Tags");
	}
	
	private void prepareNBTTagCompound(Linker linker) throws NoSuchMethodException, SecurityException {
		
		NBTTagCompound = linker.getClass("NBTTagCompound");
		NBTTagList = linker.getClass("NBTTagList");
		NBTSizeTracker = linker.getClass("NBTSizeTracker");
		
		c_NBTTagCompound = NBTTagCompound.getDeclaredConstructor();
		c_NBTSizeTracker = NBTSizeTracker.getDeclaredConstructor(long.class);
		
		m_load = NBTTagCompound.getDeclaredMethod(Constants.NBTTAGCOMPOUND_LOAD, DataInput.class, int.class, NBTSizeTracker);
		m_load.setAccessible(true);
		
		m_write = linker.method("write", NBTTagCompound, DataOutput.class);
		
		// DEBUG
		m_getTagList = linker.method("getTagList", NBTTagCompound, String.class, int.class);
		m_getCompoundTagAt = linker.method("getCompoundTagAt", NBTTagList, int.class);
	}
	
	public Object newInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object instance = c_NBTTagCompound.newInstance();
		
		return instance;		
	}
	
	public Object getInstance(DataInput input, int complexity) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object nbtSizeTracker = c_NBTSizeTracker.newInstance(MAXSIZE);
		Object instance = c_NBTTagCompound.newInstance();
		
		m_load.invoke(instance, new Object[]{input, complexity, nbtSizeTracker});
		
		return instance;
	}
	
	public Object getMinecraftTagFromTag(Tag tag) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		tag.writePayload(new DataOutputStream(cbb.getOutputStream()));
		Object mcTag = getInstance(new DataInputStream(cbb.getInputStream()), 0);
		
		if (Constants.DEBUG_TAG_COMPOUND)
			System.out.println("In:  " + mcTag);
		
		return mcTag;
	}
	
	public Tag getTagFromMinecraftTag(Object mcTag) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		if (Constants.DEBUG_TAG_COMPOUND)
			System.out.println("Out: " + mcTag);
		
		CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		m_write.invoke(mcTag, new DataOutputStream(cbb.getOutputStream()));
		Tag schemTag = Tag.createCompountTag(new DataInputStream(cbb.getInputStream()));
		
		return schemTag;
	}
	
	public Object getTagList(Object tag, String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object returnTag = m_getTagList.invoke(tag, name, 10);
		
		return returnTag;
	}
	
	public Object getCompoundTagAtObject(Object tag, int pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object compoundTag = m_getCompoundTagAt.invoke(tag, pos);
		
		return compoundTag;
	}

}
