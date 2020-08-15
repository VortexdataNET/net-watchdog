package net.vortexdata.netwatchdog.console.commands;


import net.vortexdata.netwatchdog.NetWatchdog;
import org.jline.reader.impl.completer.ArgumentCompleter;

public class ExitCommand extends BaseCommand {

    public ExitCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "exit", "Exit and close the app.");
    }

    protected ArgumentCompleter populateArgumentCompleter() {
        return null;
    }

    @Override
    public void call(String[] args) {
        netWatchdog.shutdown();
    }
}
