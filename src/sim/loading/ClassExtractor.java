package sim.loading;

import logging.Log;
import sim.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Extracts the classes from minecraft.jar
 */
class ClassExtractor {

	/**
	 * Extracts the classes from the jar
	 * @param _jarFile The correct minecraft.jar file
	 * @param reqClasses The classes to pull from it
	 * @return A HashMap with < name, class object >
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public HashMap<String, Class<?>> extractClasses(File _jarFile, HashMap<String, String> reqClasses) throws ClassNotFoundException, IOException {
		
		HashMap<String, Class<?>> classes = new HashMap<>();

        Enumeration<JarEntry> jarEntries;
		URLClassLoader cl;

        try (JarFile jarFile = new JarFile(_jarFile)) {
            jarEntries = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + _jarFile.getPath() + "!/")};
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

                    Log.e("Class " + className + " was not found.");

                    throw e;
                }
            }

        }
		
		return classes;
	}

    public void addLibaryClasses(String minecraftFolder, String[][] libraryClasses, HashMap<String, Class<?>> classes) throws IOException, ClassNotFoundException {

        for (String[] line : libraryClasses) {
            if (line.length != 3)
                throw new IllegalStateException("Malformed library classes array in Constants.");

            File jarFile = new File(minecraftFolder + Constants.LIBRARYFOLDER + line[2]);
            Class<?> classy = getClassFromJar(line[0], line[1], jarFile);

            classes.put(line[0], classy);
        }
    }

    Class<?> getClassFromJar(String requiredClassName, String _package, File _jarFile) throws IOException, ClassNotFoundException {

        Enumeration<JarEntry> jarEntries;
        URLClassLoader cl;

        String fullClass = _package + requiredClassName;

        try (JarFile jarFile = new JarFile(_jarFile)) {
            jarEntries = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + _jarFile.getPath() + "!/")};
            cl = URLClassLoader.newInstance(urls);

            while (jarEntries.hasMoreElements()) {

                try {
                    JarEntry jarEntry = jarEntries.nextElement();
                    if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class"))
                        continue;

                    // -6 because of .class
                    String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);

                    if (className.equals(fullClass))
                        return cl.loadClass(className.replace('/', '.'));

                } catch (ClassNotFoundException e) {

                    if (requiredClassName != null)
                        Log.e("Class " + requiredClassName + " was not found.");
                    else
                        Log.e("Some class was not found");

                    throw e;
                }
            }

        }

        return null;
    }
}
