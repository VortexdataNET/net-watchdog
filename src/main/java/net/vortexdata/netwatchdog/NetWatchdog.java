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

package net.vortexdata.netwatchdog;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import net.vortexdata.netwatchdog.modules.console.logging.Log;
import net.vortexdata.netwatchdog.modules.parameters.ParameterRegister;
import net.vortexdata.netwatchdog.modules.config.ConfigRegister;
import net.vortexdata.netwatchdog.modules.config.configs.BaseConfig;
import net.vortexdata.netwatchdog.modules.config.configs.NorthstarConfig;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import net.vortexdata.netwatchdog.modules.console.cli.CommandRegister;
import net.vortexdata.netwatchdog.modules.console.cli.ConsoleThread;
import net.vortexdata.netwatchdog.modules.console.cli.JLineAppender;
import net.vortexdata.netwatchdog.utils.BootUtils;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.northstar.NorthstarRegister;
import net.vortexdata.netwatchdog.modules.query.Query;
import net.vortexdata.netwatchdog.modules.updater.UpdateManager;
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
 * @version 0.2.0
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
    private UpdateManager updateManager;

    public static void main(String[] args) {
        NetWatchdog netWatchdog = new NetWatchdog();
        netWatchdog.launch(args);
    }

    public void launch(String[] args) {

        BootUtils.bootStart = LocalDateTime.now();
        isShuttingDown = false;


        // START-SCREEN
        printCopyHeader();


        // CLI AND LOGGER INIT
        JLineAppender jLineAppender = new JLineAppender();
        jLineAppender.start();

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = loggerContext.getLogger("net.vortexdata.netwatchdog");
        Log.LOGGER = logger;
        logger.setLevel(Level.DEBUG);

        Log.info("App starting... Please wait.");


        // COMMAND REGISTER
        commandRegister = new CommandRegister(this);
        CLI.init(commandRegister);
        consoleThread = new ConsoleThread(commandRegister);


        // APP INFO
        Log.debug("Loading project info...");
        appInfo = new AppInfo();
        if (appInfo.loadProjectConfig()) {
            Log.debug("Project info loaded successfully.");
        } else {
            Log.warn("Failed to load project info! This may cause issues during runtime. Is the jar file valid? Are read and write permissions set? Please check for solution and retry.");
        }
        Log.debug("You are running version " + appInfo.getVersionName() + ".");


        // UPDATE MANAGER
        updateManager = new UpdateManager(this);


        // APP CONFIGS
        configRegister = new ConfigRegister(this);
        configRegister.loadAll();


        // COMPONENTS
        componentManager = new ComponentManager(this);
        componentManager.loadAll();


        // NORTHSTAR SYSTEM
        if (configRegister.getMainConfig().getValue().has("enableNorthstars") && configRegister.getMainConfig().getValue().getString("enableNorthstars").equalsIgnoreCase("true")) {
            if (((NorthstarConfig) configRegister.getConfigByPath(NorthstarConfig.CONFIG_PATH)).canNorthstarsBeUsed()) {
                Log.info("Enabling Northstar system.");
                northstarRegister = new NorthstarRegister(this);
            } else {
                Log.warn("Can not start Northstar system due to configuration errors.");
            }
        }


        // LET START PARAMETERS TAKE EFFECT
        paramRegister = new ParameterRegister(args, this);
        paramRegister.evaluateArguments();


        // BOOT-WRAPUP
        Log.debug("Starting boot-wrapup checks...");
        if (configRegister.didCriticalConfigFail()) {
            Log.error("Encountered a critical configuration error during boot which may cause issues at runtime.");
            shutdown();
        }


        // POST- CONFIG-UPDATE USER NOTIFICATION
        if (configRegister.wereConfigsUpdated()) {
            Log.debug("Detected updated configs.");
            for (BaseConfig c : configRegister.getUpdatedConfigs()) {
                Log.warn("Config " + c.getPath() + " has been updated.");
            }
            if (CLI.promptYesNo("Some configs have been updated (may be a result of updating the app). It is highly advised to check configs before further use. " +
                    "\n\nDo you want to stop the app?"))
                shutdown();
        }


        // START QUERY
        query = new Query(this);
        query.start();


        // END BOOT SEQUENCE
        BootUtils.bootEnd = LocalDateTime.now();
        Log.info("It took " + (int) BootUtils.getBootTimeMillis() + " ms to launch the app.");


        // START CLI
        consoleThread.start();
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
            System.out.println("CRITICAL ERROR! UNABLE TO ACCESS APP RESOURCES.");
        }
    }

    public void shutdown() {
        if (isShuttingDown)
            return;
        isShuttingDown = true;
        Log.info("Shutting down for system halt...");
        Log.info("Waiting for console thread to finish...");
        if (getConsoleThread() != null) {
            this.getConsoleThread().end();
            this.getConsoleThread().interrupt();
        }
        if (getQuery() != null) {
            this.getQuery().interrupt();
        }
        Log.info("Ending logging at " + DateUtils.getPrettyStringFromLocalDateTime(LocalDateTime.now()) + ".");
        System.exit(0);
    }

    public ch.qos.logback.classic.Logger getLogbackLogger() {
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

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public boolean isShuttingDown() {
        return isShuttingDown;
    }

    public ParameterRegister getParamRegister() {
        return paramRegister;
    }

    public static String getSysPath() {
        return "sys//";
    }
}
