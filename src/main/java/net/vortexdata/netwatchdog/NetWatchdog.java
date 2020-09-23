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

import net.vortexdata.netwatchdog.modules.config.ConfigRegister;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import net.vortexdata.netwatchdog.modules.console.cli.CommandRegister;
import net.vortexdata.netwatchdog.modules.console.cli.ConsoleThread;
import net.vortexdata.netwatchdog.modules.console.cli.JLineAppender;
import net.vortexdata.netwatchdog.modules.boothandler.Boothandler;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.northstar.NorthstarRegister;
import net.vortexdata.netwatchdog.modules.query.Query;
import net.vortexdata.netwatchdog.utils.DateUtils;
import net.vortexdata.netwatchdog.utils.Platform;
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

    private Platform platform;
    private ComponentManager componentManager;
    private NorthstarRegister northstarRegister;
    private Logger logger;
    private CommandRegister commandRegister;
    private ConsoleThread consoleThread;
    private ConfigRegister configRegister;
    private Query query;

    public static void main(String[] args) {
        NetWatchdog netWatchdog = new NetWatchdog();
        netWatchdog.launch();
    }

    public void launch() {

        Boothandler.bootStart = LocalDateTime.now();

        // Display start screen
        printCopyHeader();

        // Init. loggers and console
        JLineAppender jLineAppender = new JLineAppender();
        jLineAppender.start();
        logger = LoggerFactory.getLogger("Main");
        logger.info("App starting... Please wait.");
        commandRegister = new CommandRegister(this);
        CLI.init(commandRegister);
        consoleThread = new ConsoleThread(commandRegister, this);
        consoleThread.start();

        // Check platform compatibility
        platform = Platform.getPlatformFromString(System.getProperty("os.name"));
        if (platform == null)
            logger.warn("Looks like your operating system is not supported ("+System.getProperty("os.name")+"). This may cause issues with some of the apps systems. Please either use Windows, Linux or macOS.");
        else
            logger.debug("Platform " + platform + " detected.");

        // configs
        configRegister = new ConfigRegister(this);
        componentManager = new ComponentManager(this);
        componentManager.loadAll();

        // Northstar register
        if (configRegister.getMainConfig().getValue().has("enableNorthstars") && configRegister.getMainConfig().getValue().getString("enableNorthstars").equalsIgnoreCase("true")) {
            logger.info("Enabling Northstar system.");
            northstarRegister = new NorthstarRegister(this);
        }

        query = new Query(this);
        logger.debug("Shutdown hook registered.");

        Boothandler.bootEnd = LocalDateTime.now();

        logger.info("It took " + (int) Boothandler.getBootTimeMillis() + " ms to launch the app.");

        query.start();

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
        this.getLogger().info("Shutting down for system halt...");
        this.getLogger().info("Waiting for console thread to finish...");
        this.getConsoleThread().end();
        this.getConsoleThread().interrupt();
        this.getQuery().interrupt();
        this.getLogger().info("Ending logging at " + DateUtils.getPrettyStringFromLocalDateTime(LocalDateTime.now()) + ".");
        System.exit(0);
    }

    public Platform getPlatform() {
        return platform;
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
}
