package sim.logic;


import sim.loading.Linker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RIntHashMap {

    private Class<?> IntHashMap;
    private Method m_clearMap;

    public RIntHashMap(Linker linker) throws NoSuchMethodException {

        prepareIntHashMap(linker);
    }

    private void prepareIntHashMap(Linker linker) throws NoSuchMethodException {

        IntHashMap = linker.getClass("IntHashMap");

        m_clearMap = linker.method("clearMap", IntHashMap);
    }

    public void clearMap(Object instance) throws InvocationTargetException, IllegalAccessException {
        m_clearMap.invoke(instance);
    }
}
