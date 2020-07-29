package net.vortexdata.netwatchdog.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.CLI;

public class TargetCommand extends BaseCommand {

    public TargetCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "target", "Configure and set up targets.");
        this.args.put("create", "Creates a new target.");
        this.args.put("disable", "Disables a target.");
        this.args.put("enable", "Enables a target.");
        this.args.put("delete", "Deletes a target (must be disabled).");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create")) {

            } else if (args[0].equalsIgnoreCase("disable")) {

            } else if (args[0].equalsIgnoreCase("enable")) {

            } else if (args[0].equalsIgnoreCase("delete")) {

            } else {
                printUsage();
            }
        } else {
            printUsage();
        }
    }

}
