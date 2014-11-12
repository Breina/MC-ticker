package sim.loading;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * http://stackoverflow.com/a/60766
 * @author Allain Lalonde
 */
public class ClassPathHack {
	
	private static final Class[] PARAMETERS = new Class[] {URL.class};
	
	public static void addFolder(File file) throws IOException {
	    assert file != null;

		if (file.isDirectory())
			for (File f : file.listFiles())
				addFolder(f);	
		else
			if (file.getName().endsWith(".jar"))
				addFile(file);
	}
	
	public static void addFile(File f) throws IOException
    {
        addURL(f.toURI().toURL());
    }

    public static void addURL(URL u) throws IOException
    {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", PARAMETERS);
            method.setAccessible(true);
            method.invoke(sysloader, u);
            
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }

    }

}
