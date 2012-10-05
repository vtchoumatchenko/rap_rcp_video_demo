package demo;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Dictionary;

import org.eclipse.equinox.http.jetty.JettyCustomizer;
import org.eclipse.jetty.server.Connector;

/**
 * Jetty connectors customizer. Used to set the port number.
 * <p>
 * The following VM argument is required:
 * org.eclipse.equinox.http.jetty.customizer.class=demo.DemoJettyCustomizer
 */
public class DemoJettyCustomizer extends JettyCustomizer {

    @SuppressWarnings({ "rawtypes" })
    @Override
    public Object customizeHttpConnector(final Object connector, final Dictionary settings) {
        if (connector instanceof Connector) {
            Connector jettyConnector = (Connector)connector;
            jettyConnector.setPort(findFreePort());
        }
        return super.customizeHttpConnector(connector, settings);
    }

    private int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            /*
             * ignore
             */
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    /*
                     * ignore
                     */
                }
            }
        }
        return 0;
    }
}