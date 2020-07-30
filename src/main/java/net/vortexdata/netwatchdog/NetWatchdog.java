package net.vortexdata.netwatchdog;

import net.vortexdata.netwatchdog.config.ConfigRegister;
import net.vortexdata.netwatchdog.console.CLI;
import net.vortexdata.netwatchdog.console.CommandRegister;
import net.vortexdata.netwatchdog.console.ConsoleThread;
import net.vortexdata.netwatchdog.console.JLineAppender;
import net.vortexdata.netwatchdog.modules.boothandler.Boothandler;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class NetWatchdog {

    private ComponentManager componentManager;
    private Logger logger;
    private CommandRegister commandRegister;
    private ConsoleThread consoleThread;
    private ConfigRegister configRegister;

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

        consoleThread = new ConsoleThread(commandRegister);
        consoleThread.start();

        configRegister = new ConfigRegister(this);
        componentManager = new ComponentManager(this);
        componentManager.loadAll();

        Boothandler.bootEnd = LocalDateTime.now();
        logger.info("It took " + (int) Boothandler.getBootTimeMillis() / 100000000 + " ("+Boothandler.getBootTimeMillis()+") ms to launch the app.");

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
        logger.info("Shutting down for system halt...");
        logger.info("Waiting for console thread to finish...");
        consoleThread.end();
        consoleThread.interrupt();
        logger.info("Ending logging at " + DateUtils.getPrettyStringFromLocalDateTime(LocalDateTime.now()) + ", bye.");
        System.exit(0);
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
}
