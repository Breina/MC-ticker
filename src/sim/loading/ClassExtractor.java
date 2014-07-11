package sim.loading;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import logging.Log;

/**
 * Extracts the classes from minecraft.jar
 */
public class ClassExtractor {

	/**
	 * Extracts the classes from the jar
	 * @param _jarFile The correct minecraft.jar file
	 * @param reqClasses The classes to pull from it
	 * @return A HashMap with < name, class object >
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public HashMap<String, Class<?>> extractClasses(File _jarFile, HashMap<String, String> reqClasses) throws ClassNotFoundException, IOException {
		
		HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();

		JarFile jarFile = null;
		Enumeration<JarEntry> jarEntries;
		URLClassLoader cl;

		try {
			jarFile = new JarFile(_jarFile);
		jarEntries = jarFile.entries();

		URL[] urls = { new URL("jar:file:" + _jarFile.getPath() + "!/") };
		cl = URLClassLoader.newInstance(urls);

		while (jarEntries.hasMoreElements()) {

			String className = null;
			
			try {
				JarEntry jarEntry = jarEntries.nextElement();
				if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class"))
					continue;

				// -6 because of .class
				className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
				
				if (reqClasses.containsKey(className)) {
					String original = reqClasses.get(className);
					classes.put(original, cl.loadClass(className));
				}
			
			} catch (ClassNotFoundException e) {
				
				if (className != null)
					Log.e("Class " + className + " was not found.");
				else
					Log.e("Some class was not found");
				
				throw e;				
			}
		}
			
		} finally {
				
			jarFile.close();
				
		}
		
		return classes;
	}
}
