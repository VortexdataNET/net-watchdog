package net.vortexdata.netwatchdog.modules.query;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.config.configs.MainConfig;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.component.FallbackPerformanceClass;
import net.vortexdata.netwatchdog.modules.component.PerformanceClass;

public class Query {

    private final NetWatchdog netWatchdog;
    private final MainConfig mainConfig;
    private boolean hasStarted;
    private final ComponentManager componentManager;
    private Thread thread;

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
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (!componentManager.getComponents().isEmpty()) {
                            netWatchdog.getLogger().debug("Running check cycle...");
                            for (BaseComponent bc : componentManager.getComponents()) {
                                netWatchdog.getLogger().info("Checking component " + bc.getName() + "...");
                                PerformanceClass pc = bc.check();
                                if (pc.getClass() != FallbackPerformanceClass.class) {
                                    netWatchdog.getLogger().info("Component " + bc.getName() + "'s check returned performance class " + pc.getName() + " with response time "+pc.getLastRecordedResponseTime()+".");
                                    if (!bc.isCachePerformanceClass() || bc.isHasPerformanceClassChanged())
                                        pc.runWebhooks();
                                    else
                                        netWatchdog.getLogger().info("Component " + bc.getName() + " returned cached performance class ("+bc.getName()+") and therefor skips webhooks.");
                                } else {
                                    netWatchdog.getLogger().warn("Failed to find a suitable performance class for component " + bc.getName() + " with response time "+((FallbackPerformanceClass) pc).getResponseTime()+".");
                                }

                            }
                            netWatchdog.getLogger().debug("Check cycle finished, going to sleep.");
                        }
                        Thread.sleep(mainConfig.getValue().getInt("pollRate") * 1000);
                    } catch (InterruptedException e) {
                        netWatchdog.getLogger().debug("Query got interrupted (for shutdown?).");
                    }
                }
            }
        });
        thread.start();
    }

    public void interrupt() {
        thread.interrupt();
    }

}
