package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.utils.Platform;

import java.net.Socket;

/**
 * Wrapper class used to store Northstar information.
 *
 * @author Sandro Kierner
 * @author Michael Wiesinger
 * @version 0.0.0
 * @since 0.0.0
 */
public abstract class NorthstarBase {

    public NorthstarBase(NorthstarRegister northstarRegister, String address, int timeout, int samples) {
        this.timeout = timeout;
        this.northstarRegister = northstarRegister;
        this.address = address;
        this.samples = samples;
    }

    protected int samples;
    protected int timeout;
    protected NorthstarRegister northstarRegister;
    protected String address;

    public abstract boolean isAvailable();

}
