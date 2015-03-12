package sim.loading;

import java.util.HashMap;

// TODO Provide proper error handling for this entire class instead of returning null
/**
 * This class able to present one with the obfuscated name, should be pretty straight forward
 */
public class Translator {
	
	private final HashMap<String, String> classes;
    private final HashMap<String, String> methods;
    private final HashMap<String, String> fields;
	
	public Translator() {
		classes = new HashMap<>();
		methods = new HashMap<>();
		fields = new HashMap<>();
	}
	
	public void addClass(String original, String obfuscated) {
		classes.put(obfuscated, original);
	}
	
	public void addMethod(String original, String obfuscated) {
		
		methods.put(original, obfuscated);
	}
	
	public void addField(String original, String obfuscated) {
		fields.put(original, obfuscated);
	}
	
	public String getClass(String original) {
		return classes.get(original);
	}
	
	public String getMethod(String original) {
		return methods.get(original);
	}
	
	public String getField(String original) {
		
		return fields.get(original);
	}
	
	public HashMap<String, String> getClasses() {
		return classes;
	}
	
	public HashMap<String, String> getMethods() {
		return methods;
	}
	
	public HashMap<String, String> getFields() {
		return fields;
	}
}
