package net.vortexdata.netwatchdog.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.cli.CLI;

import java.util.HashMap;

public abstract class BaseCommand {

    protected String description;
    protected HashMap<String, String> args;
    protected String name;
    protected NetWatchdog netWatchdog;

    public BaseCommand(NetWatchdog netWatchdog, String name, String description) {
        this.netWatchdog = netWatchdog;
        this.name = name;
        args = new HashMap<>();
        this.description = description;
    }

    public String getHelpMessage() {
        StringBuilder sb = new StringBuilder();

        if (args == null || args.size() == 0)
            if (description != null && description.isEmpty())
                return description;
            else
                return "Help message unavailable.";

        sb.append(description + "\n\n");
        sb.append("Usage: ");
        sb.append(getName() + " ");

        sb.append(generateArgsString() + "\n\n");

        sb.append("More information about all arguments:\n\n");
        for (String key : args.keySet()) {
            sb.append("- " + key + ": " + args.get(key) + "\n");
        }

        return sb.toString();
    }

    private String generateArgsString() {
        if (args == null || args.size() == 0)
            return "N/A";

        String export = "<";
        String[] keys = args.keySet().toArray(new String[args.size()]);
        for (int i = 0; i < keys.length; i++) {
            if (keys.length - i == 1) {
                export += keys[i] + ">";
                break;
            } else if (i == 0) {
                export += keys[i] + " | ";
            } else {
                export += keys[i] + " | ";
            }
        }
        return export;
    }

    protected void printUsage() {
        CLI.print(getHelpMessage());
    }

    public abstract void call(String[] args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<String, String> getArgs() {
        return args;
    }
}
