package demo;

import java.util.HashMap;
import java.util.Map;

import demo.SessionSingleton;


public class SessionSingletonImpl extends SessionSingleton {

    @SuppressWarnings("rawtypes")
	private final Map<Class, Object> instances = new HashMap<Class, Object>();

    @SuppressWarnings("unchecked")
	@Override
    protected <T> T abstractGetInstance(final Class<T> type) {
        Object result = instances.get(type);
        if (result == null) {
            try {
                result = type.getClassLoader().loadClass(type.getName()).newInstance();
                instances.put(type, result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return (T)result;
    }
}