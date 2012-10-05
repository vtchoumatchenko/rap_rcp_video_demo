package demo;

import org.eclipse.rwt.RWT;

import demo.SessionSingleton;

public class HostInfoImpl extends HostInfo {

	private String host;
	private int port;
	private String context;
	private String scheme;

	private HostInfoImpl getSessionSingletonInstance() {
		return SessionSingleton.getInstance(HostInfoImpl.class);
	}

	@Override
	protected String doGetHost() {
		String host = getSessionSingletonInstance().host;
		if (host == null) {
			throw new IllegalStateException("HostInfo is not initialized.");
		}
		return host;
	}

	@Override
	protected int doGetPort() {
		return getSessionSingletonInstance().port;
	}

	@Override
	protected void doSetHost(final String host) {
		getSessionSingletonInstance().host = host;
	}

	@Override
	protected void doSetPort(final int port) {
		getSessionSingletonInstance().port = port;
	}

	@Override
	protected String doGetScheme() {
		return getSessionSingletonInstance().scheme;
	}

	@Override
	protected void doSetScheme(final String scheme) {
		getSessionSingletonInstance().scheme = scheme;
	}

	@Override
	protected String doGetContext() {
		return getSessionSingletonInstance().context;
	}

	@Override
	protected void doSetContext(final String context) {
		getSessionSingletonInstance().context = context;
	}

	@Override
	protected void init() {
		doSetHost(RWT.getRequest().getServerName());
		doSetPort(RWT.getRequest().getLocalPort());
		doSetContext(RWT.getRequest().getContextPath());
		doSetScheme(RWT.getRequest().getScheme());
	}
}
