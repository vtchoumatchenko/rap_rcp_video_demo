package demo;


public abstract class SessionSingleton {
    private static SessionSingleton _instance;

    public static <T> T getInstance(final Class<T> type) {
        if (_instance == null) {
            _instance = ImplementationLoader.newInstance(SessionSingleton.class);
        }
        return _instance.abstractGetInstance(type);
    }

    protected abstract <T> T abstractGetInstance(final Class<T> type);

}