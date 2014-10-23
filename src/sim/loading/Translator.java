package sim.loading;

import java.util.HashMap;
import java.util.Iterator;

// TODO Provide proper error handling for this entire class instead of returning null
/**
 * This class able to present one with the obfuscated name, should be pretty straight forward
 */
public class Translator {
	
	private HashMap<String, String> classes, methods, fields;
	
	public Translator() {
		classes = new HashMap<String, String>();
		methods = new HashMap<String, String>();
		fields = new HashMap<String, String>();
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
