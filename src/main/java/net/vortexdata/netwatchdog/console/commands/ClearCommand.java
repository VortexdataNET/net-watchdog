package net.vortexdata.netwatchdog.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.cli.CLI;

public class ClearCommand extends BaseCommand {

    public ClearCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "clear", "Clears the console screen.");
    }

    @Override
    public void call(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200; i++)
            sb.append("\n");
        CLI.print(sb.toString());
    }

}
