package net.vortexdata.netwatchdog.console.commands;


import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.CLI;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.slf4j.Logger;

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
