package sim.logic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import logging.Log;
import sim.constants.Constants;
import sim.loading.Linker;

public class RProfiler {
	
	private Class<?> Profiler;
	private Object profiler;
	private Field f_profilingMap;
	private Method m_startSection, m_stopSection;

	private static RProfiler instance;
	
	public RProfiler(Linker linker) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, NoSuchMethodException {
		
		prepareProfiler(linker);

		instance = this;
		
		Log.i("Preparing MC's Profiler");
	}
	
	private void prepareProfiler(Linker linker) throws NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, NoSuchMethodException {
		
		Profiler = linker.getClass("Profiler");
		profiler = Profiler.newInstance();
		f_profilingMap = linker.field("profilingMap", Profiler);
		
		Field f_profilingEnabled = linker.field("profilingEnabled", Profiler);
		f_profilingEnabled.setBoolean(profiler, true);
		
		m_startSection = linker.method("startSection", Profiler, String.class);
		m_stopSection = linker.method("endSection", Profiler);
	}
	
	public void testProfiler(String msg) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		m_startSection.invoke(profiler, msg);
		m_stopSection.invoke(profiler);
	}

	public static void print() {

		try {
			instance.printOutput();

		} catch (IllegalArgumentException | IllegalAccessException e) {

			Log.e("Failed to print MC profiler: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void printOutput() throws IllegalArgumentException, IllegalAccessException {
		
		System.out.println("\nMINECRAFT PROFILER:");
		
		HashMap<String, Long> profilingMap = (HashMap<String, Long>) f_profilingMap.get(profiler);
		
		Iterator<Entry<String, Long>> i = profilingMap.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		
		while (i.hasNext()) {
			
			Entry<String, Long> entry = (Entry<String, Long>) i.next();
			
			String key = entry.getKey();
			sb.append(key);
			
			int extraSpace = Constants.OUTPUT_INDENT - key.length();
			for (int col = 0; col < extraSpace; col++)
				sb.append(' ');
			
			sb.append(entry.getValue());
			sb.append('\n');
		}
		
		System.out.println(sb.toString());
		
	}
	
	public Object getInstance() {
		return profiler;
	}
}
