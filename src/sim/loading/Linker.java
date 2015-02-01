package sim.loading;

import logging.Log;
import sim.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Just an intermediate class once again that makes writing high level Simulation code more elegant, so we can avoid some reflection
 */
public class Linker {
	
	private Translator translator;
	
	// All buffered class objects
	private HashMap<String, Class<?>> classes;
	
	/**
	 * Deobfuscating is a 2 step process, so the intermediate is temporarily buffered
	 * @throws ClassNotFoundException 
	 */
	public Linker(String mcpFolder, String minecraftFolder) throws IOException, ClassNotFoundException {
		
		ClassPathHack.addFolder(new File(minecraftFolder + Constants.LIBRARYFOLDER));
		
		// Parses the methods and fields .csv file
		CSVparser parser = new CSVparser();

		HashMap<String, String> o_methods = parser.readDeobfuscation(new File(mcpFolder + File.separator + Constants.METHODSCSV),
				Constants.REQUIREDMETHODS);
		HashMap<String, String> o_fields = parser.readDeobfuscation(new File(mcpFolder + File.separator + Constants.FIELDSCSV),
				Constants.REQUIREDFIELDS);
		
		// Parses the joined.srg file
		JoinedParser joinedParser = new JoinedParser();

		// Creates the translator
		translator = joinedParser.readDeobfuscation(new File(mcpFolder + File.separator + Constants.JOINEDSRG),
				Constants.REQUIREDCLASSES, o_methods, o_fields);
		
		// Extracts and buffers the required classes from minecraft.jar
		ClassExtractor extractor = new ClassExtractor();
		classes = extractor.extractClasses(new File(minecraftFolder + Constants.MINECRAFTJAR), translator.getClasses());

		Log.i("Parsing and extracting");
	}
	
	/**
	 * Gets the required class
	 * @param name Its name
	 * @return The class
	 */
	public Class<?> getClass(String name) {
		return classes.get(name);
	}
	
	/**
	 * Gets the requested method
	 * @param name Its name
	 * @param clazz The class object of where to search in
	 * @param parameterTypes The method's parameterTypes
	 * @return The method
	 */
	public Method method(String name, Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		
		Method m = clazz.getDeclaredMethod(translator.getMethod(name), parameterTypes);
		m.setAccessible(true);
		return m;
	}

	/**
	 * Gets the requested method
	 * @param name Its name
	 * @param clazz The class object of where to search in
	 * @return The field
	 */
	public Field field(String name, Class<?> clazz) throws NoSuchFieldException, SecurityException {
		Field f = clazz.getDeclaredField(translator.getField(name));
		f.setAccessible(true);
		return f;
	}
}