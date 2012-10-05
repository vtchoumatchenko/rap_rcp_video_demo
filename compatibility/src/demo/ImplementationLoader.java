package demo;

import java.text.MessageFormat;

public class ImplementationLoader {

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(final Class<T> type) {
        String name = type.getName();
        T result = null;
        try {
            result = (T)type.getClassLoader().loadClass(name + "Impl").newInstance(); 
            if (!type.isAssignableFrom(result.getClass())) {
                throw new IllegalArgumentException("Unable to assign");} 
        } catch (Throwable throwable) {
            String txt = "Could not load implementation for {0}"; 
            String msg = MessageFormat.format(txt, new Object[] { name });
            throw new RuntimeException(msg, throwable);
        }
        return result;
    }
}
