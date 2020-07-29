package net.vortexdata.netwatchdog.console.commands;


import net.vortexdata.netwatchdog.console.CLI;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.slf4j.Logger;

public class ExitCommand extends BaseCommand {

    public ExitCommand(Logger logger) {
        super(logger, "exit");
    }

    protected ArgumentCompleter populateArgumentCompleter() {
        return null;
    }

    @Override
    public void call(String[] args) {
        CLI.print("Arguments: " + args.length);
    }
}
