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
public class Northstar {

    public Northstar(NorthstarRegister northstarRegister, String address) {
        this.northstarRegister = northstarRegister;
        this.address = address;
    }

    private NorthstarRegister northstarRegister;
    private String address;

    public boolean isAvailable() {

        // TODO: Implement os ping system

        return false;

    }

}
