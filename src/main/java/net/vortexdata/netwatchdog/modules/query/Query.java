/*
 * MIT License
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
import net.vortexdata.netwatchdog.modules.config.configs.MainConfig;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.component.FallbackPerformanceClass;
import net.vortexdata.netwatchdog.modules.component.PerformanceClass;
import net.vortexdata.netwatchdog.modules.config.configs.NorthstarConfig;
import net.vortexdata.netwatchdog.modules.console.logging.Log;
import net.vortexdata.netwatchdog.modules.northstar.NorthstarBase;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Query thread responsible for periodically checking all components.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
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

    /**
     * Runs Northstar and components checks.
     */
    private void runChecks() {

        int threadCount = 1;
        int terminationThreshold = netWatchdog.getConfigRegister().getMainConfig().getValue().getInt("threadTerminationThreshold");
        if (terminationThreshold < 1)
            terminationThreshold = 60;

        try {
            threadCount = netWatchdog.getConfigRegister().getMainConfig().getValue().getInt("threadCount");
            if (threadCount < 1) {
                Log.error("Invalid value for key \"threadCount\" in main config, must be numeric and higher than 0.");
            }
        } catch (Exception e) {
            Log.error("Invalid value for key \"threadCount\" in main config, must be numeric and higher than 0.");
            threadCount = 1;
        }

        if (threadCount > 1) {
            Log.debug("Running check cycle multithreaded ("+threadCount+" threads)...");
            ExecutorService tpe = Executors.newFixedThreadPool(threadCount);
            for (BaseComponent bc : componentManager.getComponents()) {
                tpe.submit(() -> checkComponent(bc));
            }
            tpe.shutdown();
            try {
                tpe.awaitTermination(terminationThreshold, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.error("Query thread termination threshold exceeded, appending error message: " + e.getMessage());
            }
        } else {
            Log.info("Running sequential check cycle...");
            for (BaseComponent bc : componentManager.getComponents()) {
                checkComponent(bc);
            }
        }
        Log.info("Check cycle finished.");
    }

    private void checkComponent(BaseComponent bc) {
        Log.info("Checking component " + bc.getFilename() + "...");
        try {
            PerformanceClass pc = bc.check();
            if (pc.getClass() != FallbackPerformanceClass.class) {
                Log.info("Component " + bc.getFilename() + "'s check returned performance class " + pc.getName() + " with response time "+pc.getLastRecordedResponseTime()+".");
                if (!bc.isCachePerformanceClass() || bc.isHasPerformanceClassChanged())
                    pc.runWebhooks();
                else
                    Log.debug("Component " + bc.getFilename() + " returned cached performance class ("+bc.getFilename()+") and therefore skips webhooks.");
            } else {
                Log.warn("Failed to find a suitable performance class for component " + bc.getFilename() + " with response time "+((FallbackPerformanceClass) pc).getResponseTime()+".");
            }
        } catch (Exception e) {
            Log.error("Failed to check component " + bc.getFilename() + ": " + e.getMessage());
        }
    }

    public void start() {
        if (hasStarted)
            return;
        hasStarted = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (netWatchdog.getNorthstarRegister() != null && netWatchdog.getAppInfo().getPlatform() == null)
                    Log.warn("The Northstar system has been disabled as it is not supported on your operating system.");
                while (true) {
                    try {
                        if (!componentManager.getComponents().isEmpty()) {
                            if (netWatchdog.getNorthstarRegister() != null && netWatchdog.getAppInfo().getPlatform() != null) {
                                int neededPercent = netWatchdog.getConfigRegister().getConfigByPath(NorthstarConfig.CONFIG_PATH).getValue().getInt("availPercentMin");
                                int actualPercent = netWatchdog.getNorthstarRegister().getAvailabilityPercentage();
                                if (actualPercent >= neededPercent) {
                                    runChecks();
                                } else {
                                    Log.warn("Northstar results insufficient to run check cycle (got "+actualPercent+"%, expecting "+neededPercent+"%), skipping.");
                                }
                            } else {
                                runChecks();
                            }
                        }
                        Thread.sleep(mainConfig.getValue().getInt("pollDelay") * 1000);
                    } catch (InterruptedException e) {
                        Log.debug("Query got interrupted (for shutdown?).");
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
