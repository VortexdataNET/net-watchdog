package net.vortexdata.netwatchdog.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.CLI;
import org.fusesource.jansi.Ansi;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Colors;

public class HelpCommand extends BaseCommand {

    public HelpCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "help", "Get a list of all commands.");
        this.args.put("<command>", "Creates a new target.");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            BaseCommand c = netWatchdog.getCommandRegister().getCommandByName(args[1]);
            if (c != null) {
                c.printUsage();
            } else {
                CLI.print("Unknown command.");
            }
        } else {
            AttributedStringBuilder builder = new AttributedStringBuilder();
            builder.append("The following commands are supported at the moment:\n\n");
            StringBuilder sb = new StringBuilder();
            for (BaseCommand c : netWatchdog.getCommandRegister().getCommands()) {
                sb.append(String.format("%-32s%-32s", c.getName(), c.getDescription()) + "\n");
            }

            CLI.print(builder.toAnsi());
            CLI.print(sb.toString());
        }
    }

}
