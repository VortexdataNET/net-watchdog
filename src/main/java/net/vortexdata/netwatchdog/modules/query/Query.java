/*
 * NET Watchdog
 *
 * Copyright (c) 2020 VortexdataNET
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.vortexdata.netwatchdog.modules.query;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.config.configs.MainConfig;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.component.FallbackPerformanceClass;
import net.vortexdata.netwatchdog.modules.component.PerformanceClass;

/**
 * Query thread responsible for periodically checking all components.
 *
 * @author  Sandro Kierner
 * \@since 0.0.1
 * \\\@version 0.0.3
 */
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
