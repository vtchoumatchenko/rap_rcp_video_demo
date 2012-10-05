package demo;


public abstract class HostInfo  {

    private static HostInfo _instance;

    public void initialize() {
        init();
    }

    protected abstract void init();

    public static HostInfo getInstance() {
        if (_instance == null) {
            _instance = ImplementationLoader.newInstance(HostInfo.class);
        }
        return _instance;
    }

    public String getHost() {
        String host = doGetHost();
        return host;
    }

    public int getPort() {
        return doGetPort();
    }

    public String getContext() {
        return doGetContext();
    }

    protected abstract String doGetScheme();

    protected abstract String doGetHost();

    protected abstract int doGetPort();

    protected abstract String doGetContext();

    protected abstract void doSetScheme(final String scheme);

    protected abstract void doSetHost(final String host);

    protected abstract void doSetPort(final int port);

    protected abstract void doSetContext(final String context);
}
