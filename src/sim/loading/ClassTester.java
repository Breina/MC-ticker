package sim.loading;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ClassTester {

	/**
	 * Just displays all methods of a class nicely
	 * @param c The class
	 */
	public static void test(Class<?> c) {
		
		System.out.println("\n\nTesting class " + c.getName());
		
		Field[] fields = c.getDeclaredFields();
		Method[] methods = c.getDeclaredMethods();
		Constructor<?>[] cons = c.getDeclaredConstructors();
		
		System.out.println("\nFields:");
		for (Field f : fields)
			System.out.println(f.toGenericString());
		
		System.out.println("\nConstructors:");
		for (Constructor<?> con : cons)
			System.out.println(con.toGenericString());
		
		System.out.println("\nMethods:");
		for (Method m : methods)
			System.out.println(m.toGenericString());
	}
}
