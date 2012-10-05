package demo;

import org.eclipse.rwt.SessionSingletonBase;

import demo.SessionSingleton;

public class SessionSingletonImpl extends SessionSingleton {

    @Override
    protected <T> T abstractGetInstance(final Class<T> type) {
        return (T)SessionSingletonBase.getInstance(type);
    }

}