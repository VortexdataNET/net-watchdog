package net.vortexdata.netwatchdog.modules.northstar;

import java.io.IOException;
import java.net.Socket;

/**
 * Socket Northstar class.
 *
 * @author Sandro Kierner
 * @version 0.2.0
 * @since 0.1.0
 */
public class SocketNorthstar extends NorthstarBase {

    private int port;

    public SocketNorthstar(NorthstarRegister northstarRegister, String address, int timeout, int port) {
        super(northstarRegister, address, timeout, 1);
        this.port = port;
    }

    @Override
    public boolean isAvailable() {

        long start = System.currentTimeMillis();

        try {
            Socket s = new Socket(address, port);
            s.close();
            long end = System.currentTimeMillis();
            if (end-start >= timeout)
                return false;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
