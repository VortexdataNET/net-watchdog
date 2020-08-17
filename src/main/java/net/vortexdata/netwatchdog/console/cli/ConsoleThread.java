package net.vortexdata.netwatchdog.console.cli;

import net.vortexdata.netwatchdog.NetWatchdog;

public class ConsoleThread extends Thread {

    private CommandRegister commandRegister;
    private boolean active;
    private NetWatchdog netWatchdog;

    public ConsoleThread(CommandRegister commandRegister, NetWatchdog netWatchdog) {
        active = true;
        this.commandRegister = commandRegister;
        this.netWatchdog = netWatchdog;
    }

    @Override
    public void run() {
        active = true;
        while (active) {
            String input = "";
            try {
                input = CLI.readLine("> ");
                if (!commandRegister.evaluateCommand(input))
                    CLI.print(input.split(" ")[0] + ": Command not found");
            } catch (Exception e) {
                netWatchdog.shutdown();
            }
        }
    }

    public void end() {
        active = false;
    }

}
