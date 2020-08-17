package net.vortexdata.netwatchdog.utils;

import net.vortexdata.netwatchdog.NetWatchdog;

import java.time.LocalDateTime;

public class ShutdownHook extends Thread {

    private NetWatchdog netWatchdog;
    private boolean hasStarted;

    public ShutdownHook(NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
        hasStarted = false;
    }

    @Override
    public void run() {
        if (hasStarted)
            return;

        hasStarted = true;
        netWatchdog.getLogger().info("Shutting down for system halt...");
        netWatchdog.getLogger().info("Waiting for console thread to finish...");
        netWatchdog.getConsoleThread().end();
        netWatchdog.getConsoleThread().interrupt();
        netWatchdog.getQuery().interrupt();
        netWatchdog.getLogger().info("Ending logging at " + DateUtils.getPrettyStringFromLocalDateTime(LocalDateTime.now()) + ".");
        System.exit(0);
    }

}
