package demo;

import demo.HostInfo;

public class HostInfoImpl extends HostInfo {

    private String scheme = "http"; //$NON-NLS-1$
    private String host = "localhost"; //$NON-NLS-1$
    private String context = ""; //$NON-NLS-1$

    @Override
    protected String doGetHost() {
        return host;
    }

    @Override
    protected int doGetPort() {
        int port = Activator.getDefault().getServerPort();
        return port;
    }

    @Override
    protected void doSetHost(final String host) {
        this.host = host;
    }

    @Override
    protected void doSetPort(final int port) {
        // do nothing
    }

    @Override
    protected String doGetContext() {
        return context;
    }

    @Override
    protected void doSetContext(final String context) {
        this.context = context;
    }

    @Override
    protected void init() {
        // empty
    }

    @Override
    protected String doGetScheme() {
        return scheme;
    }

    @Override
    protected void doSetScheme(String scheme) {
        this.scheme = scheme;
    }
}