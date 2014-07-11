package sim.loading;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public class ClassTester {

	/**
	 * Just displays all methods of a class nicely
	 * @param c The class
	 */
	public static void test(Class<?> c) {
		
		System.out.println("\n\nTesting class " + c.getName());
		
		Method[] methods = c.getMethods();
		Constructor<?>[] cons = c.getConstructors();
		
		System.out.println("\nConstructors:");
		
		for (Constructor<?> con : cons)
			System.out.println(con.toString());
		
		System.out.println("\nMethods:");
		for (Method m : methods)
			System.out.println(m.toGenericString());
	}
}
