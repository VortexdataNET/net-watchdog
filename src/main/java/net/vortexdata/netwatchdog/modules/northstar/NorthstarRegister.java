package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.NetWatchdog;
import sun.nio.ch.Net;

import java.util.ArrayList;

public class NorthstarRegister {

    private ArrayList<Northstar> northstars;
    private NetWatchdog netWatchdog;

    public NorthstarRegister(NetWatchdog netWatchdog) {
        this.netWatchdog = new NetWatchdog();
    }

    public double getAvailabilityPercentage() throws PlatformNotSupportedException {
        if (netWatchdog.getPlatform() == null)
            throw new PlatformNotSupportedException("Your operating systems ping program is not supported by the app.");
        return 0;
    }

}
