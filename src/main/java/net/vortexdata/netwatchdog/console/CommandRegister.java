package net.vortexdata.netwatchdog.console;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.commands.*;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandRegister {

    ArrayList<BaseCommand> commands;
    private NetWatchdog netWatchdog;

    public CommandRegister(NetWatchdog netWatchdog) {
        commands = new ArrayList<>();
        this.netWatchdog = netWatchdog;
        registerCommands();
    }

    public boolean evaluateCommand(String command) {
        String[] parts = command.split(" ");
        BaseCommand c = getCommandByName(parts[0]);
        if (c != null) {
            c.call(Arrays.copyOfRange(parts, 1, parts.length));
            return true;
        }
        return false;
    }

    private void registerCommands() {
        registerCommand(new ExitCommand(netWatchdog));
        registerCommand(new ComponentCommand(netWatchdog));
        registerCommand(new ComponentsCommand(netWatchdog));
        registerCommand(new HelpCommand(netWatchdog));
        registerCommand(new ClearCommand(netWatchdog));
    }

    public boolean registerCommand(BaseCommand command) {
        if (getCommandByName(command.getName()) != null)
            return false;
        commands.add(command);
        return true;
    }

    public BaseCommand getCommandByName(String name) {
        for (BaseCommand c : commands) {
            if (c.getName().equalsIgnoreCase(name))
                return c;
        }
        return null;
    }

    public ArgumentCompleter getCommandNameArgumentCompleter() {
        String[] names = new String[commands.size()];
        int i = 0;
        for (BaseCommand c : commands) {
            names[i] = c.getName();
            i++;
        }
        return new ArgumentCompleter(new StringsCompleter(names));
    }

    public ArrayList<BaseCommand> getCommands() {
        return commands;
    }
}
