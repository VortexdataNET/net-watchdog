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

package net.vortexdata.netwatchdog;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import net.vortexdata.netwatchdog.modules.arguments.ParameterRegister;
import net.vortexdata.netwatchdog.modules.config.ConfigRegister;
import net.vortexdata.netwatchdog.modules.config.configs.NorthstarConfig;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import net.vortexdata.netwatchdog.modules.console.cli.CommandRegister;
import net.vortexdata.netwatchdog.modules.console.cli.ConsoleThread;
import net.vortexdata.netwatchdog.modules.console.cli.JLineAppender;
import net.vortexdata.netwatchdog.modules.boothandler.Boothandler;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.northstar.NorthstarRegister;
import net.vortexdata.netwatchdog.modules.query.Query;
import net.vortexdata.netwatchdog.utils.AppInfo;
import net.vortexdata.netwatchdog.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

/**
 * Main class of NET Watchdog app.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.0.1
 */
public class NetWatchdog {

    private boolean isShuttingDown;
    private ComponentManager componentManager;
    private NorthstarRegister northstarRegister;
    private ch.qos.logback.classic.Logger logger;
    private CommandRegister commandRegister;
    private ConsoleThread consoleThread;
    private ConfigRegister configRegister;
    private Query query;
    private AppInfo appInfo;
    private ParameterRegister paramRegister;

    public static void main(String[] args) {
        NetWatchdog netWatchdog = new NetWatchdog();
        netWatchdog.launch(args);
    }

    public void launch(String[] args) {

        Boothandler.bootStart = LocalDateTime.now();
        isShuttingDown = false;

        // Display start screen
        printCopyHeader();

        // Init. loggers and console
        JLineAppender jLineAppender = new JLineAppender();
        jLineAppender.start();

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = loggerContext.getLogger("net.vortexdata.netwatchdog");
        logger.setLevel(Level.DEBUG);

        logger.info("App starting... Please wait.");

        commandRegister = new CommandRegister(this);
        CLI.init(commandRegister);
        consoleThread = new ConsoleThread(commandRegister, this);
        consoleThread.start();

        // Load project info
        logger.debug("Loading project info...");
        appInfo = new AppInfo();
        if (appInfo.loadProjectConfig()) {
            logger.debug("Project info loaded successfully.");
        } else {
            logger.warn("Failed to load project info! This may cause issues during runtime. Is the jar file valid? Are read and write permissions set? Please check for solution and retry.");
        }
        logger.debug("You are running version " + appInfo.getVersionName() + ".");

        // Inform user about platform
        if (appInfo.getPlatform() == null)
            logger.warn("Looks like your operating system is not supported ("+System.getProperty("os.name")+"). This may cause issues with some of the apps systems. Please either use Windows, Linux or macOS.");
        else
            logger.debug("Platform " + appInfo.getPlatform() + " detected.");

        // configs
        configRegister = new ConfigRegister(this);
        componentManager = new ComponentManager(this);
        componentManager.loadAll();

        // Northstar register
        if (configRegister.getMainConfig().getValue().has("enableNorthstars") && configRegister.getMainConfig().getValue().getString("enableNorthstars").equalsIgnoreCase("true")) {
            if (((NorthstarConfig) configRegister.getConfigByPath(NorthstarConfig.CONFIG_PATH)).canNorthstarsBeUsed()) {
                logger.info("Enabling Northstar system.");
                northstarRegister = new NorthstarRegister(this);
            } else {
                logger.warn("Can not start Northstar system due to configuration errors.");
            }
        }

        // Check start parameters
        paramRegister = new ParameterRegister(args, this);
        paramRegister.evaluateArguments();

        // Boot-wrapup checks
        logger.debug("Starting boot-wrapup checks.");
        if (configRegister.didCriticalConfigFail()) {
            logger.error("Encountered a critical configuration error during boot which may cause issues at runtime.");
            shutdown();
        }

        // Init query
        query = new Query(this);
        query.start();

        // End boot sequence
        Boothandler.bootEnd = LocalDateTime.now();
        logger.info("It took " + (int) Boothandler.getBootTimeMillis() + " ms to launch the app.");
    }

    public void printCopyHeader() {
        BufferedReader headBr = null;
        try {
            InputStream headIs = getClass().getResourceAsStream("/startup-header.txt");
            headBr = new BufferedReader(new InputStreamReader(headIs));
            while (headBr.ready()) {
                System.out.println(headBr.readLine());
            }
        } catch (Exception e) {
            System.out.println("Can't display credits... :(");
        }
    }

    public void shutdown() {
        if (isShuttingDown)
            return;
        isShuttingDown = true;
        this.getLogger().info("Shutting down for system halt...");
        this.getLogger().info("Waiting for console thread to finish...");
        if (getConsoleThread() != null) {
            this.getConsoleThread().end();
            this.getConsoleThread().interrupt();
        }
        if (getQuery() != null) {
            this.getQuery().interrupt();
        }
        this.getLogger().info("Ending logging at " + DateUtils.getPrettyStringFromLocalDateTime(LocalDateTime.now()) + ".");
        System.exit(0);
    }

    public ch.qos.logback.classic.Logger getLogbackLogger() {
        return logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }

    public ConsoleThread getConsoleThread() {
        return consoleThread;
    }

    public ConfigRegister getConfigRegister() {
        return configRegister;
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public Query getQuery() {
        return query;
    }

    public NorthstarRegister getNorthstarRegister() {
        return northstarRegister;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }
}
