package net.vortexdata.netwatchdog.modules.query;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.config.configs.MainConfig;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.component.PerformanceClass;

public class Query {

    private NetWatchdog netWatchdog;
    private MainConfig mainConfig;
    private boolean hasStarted;
    private ComponentManager componentManager;

    public Query(NetWatchdog netWatchdog) {
        hasStarted = false;
        this.netWatchdog = netWatchdog;
        this.mainConfig = netWatchdog.getConfigRegister().getMainConfig();
        this.componentManager = netWatchdog.getComponentManager();
    }

    public void start() {
        if (hasStarted)
            return;
        hasStarted = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        netWatchdog.getLogger().debug("Running check cycle...");
                        for (BaseComponent bc : componentManager.getComponents()) {
                            netWatchdog.getLogger().info("Checking component " + bc.getName() + "...");
                            PerformanceClass pc = bc.check();
                            netWatchdog.getLogger().info("Component " + bc.getName() + "'s check returned performance class " + pc.getName() + ".");
                            pc.runWebhooks();
                        }
                        netWatchdog.getLogger().debug("Check cycle finished, going to sleep.");
                        Thread.sleep(mainConfig.getValue().getInt("pollRate") * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
