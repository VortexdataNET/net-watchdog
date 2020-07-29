package net.vortexdata.netwatchdog;

import net.vortexdata.netwatchdog.console.CLI;
import net.vortexdata.netwatchdog.console.CommandRegister;
import net.vortexdata.netwatchdog.console.ConsoleThread;
import net.vortexdata.netwatchdog.console.JLineAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NetWatchdog {

    public static void main(String[] args) {
        NetWatchdog netWatchdog = new NetWatchdog();
        netWatchdog.launch();
    }

    public void launch() {
        // Display start screen
        printCopyHeader();

        // Init. loggers and console
        JLineAppender jLineAppender = new JLineAppender();
        jLineAppender.start();
        Logger logger = LoggerFactory.getLogger("Main");
        CommandRegister commandRegister = new CommandRegister(logger);
        CLI.init(commandRegister);
        ConsoleThread consoleThread = new ConsoleThread(commandRegister);
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
            System.out.println("Can't display credits... :(");
        }
    }

}
