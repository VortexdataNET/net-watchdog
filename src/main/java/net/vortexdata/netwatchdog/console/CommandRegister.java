package net.vortexdata.netwatchdog.console;

import net.vortexdata.netwatchdog.console.commands.BaseCommand;
import net.vortexdata.netwatchdog.console.commands.ExitCommand;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandRegister {

    ArrayList<BaseCommand> commands;
    Logger logger;

    public CommandRegister(Logger logger) {
        commands = new ArrayList<>();
        registerCommands();
        this.logger = logger;
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
        registerCommand(new ExitCommand(logger));
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

}
