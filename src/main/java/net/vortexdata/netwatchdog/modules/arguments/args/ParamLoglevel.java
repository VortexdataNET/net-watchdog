package net.vortexdata.netwatchdog.modules.arguments.args;

import ch.qos.logback.classic.Level;
import net.vortexdata.netwatchdog.NetWatchdog;

public class ParamLoglevel extends ParamBase {

    public ParamLoglevel() {
        super("logLevel", "ll");
    }

    @Override
    public boolean runPreparation(String[] args, String calledName, NetWatchdog netWatchdog) {

        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) netWatchdog.getLogger();

        if (args.length == 0) {
            netWatchdog.getLogger().warn("Missing log level argument for start parameter "+calledName+", falling back to log level INFO.");
            logger.setLevel(Level.INFO);
            return false;
        } else {
            if (args[0].equalsIgnoreCase("DEBUG")) {
                netWatchdog.getLogger().debug("Using log level DEBUG.");
                logger.setLevel(Level.DEBUG);
            } else if (args[0].equalsIgnoreCase("INFO")) {
                netWatchdog.getLogger().debug("Using log level INFO.");
                logger.setLevel(Level.INFO);
            } else if (args[0].equalsIgnoreCase("WARN")) {
                netWatchdog.getLogger().debug("Using log level WARN.");
                logger.setLevel(Level.WARN);
            } else if (args[0].equalsIgnoreCase("ERROR")) {
                netWatchdog.getLogger().debug("Using log level ERROR.");
                logger.setLevel(Level.ERROR);
            } else {
                netWatchdog.getLogger().debug("Unknown log level "+args[0]+", falling back to log level INFO.");
                logger.setLevel(Level.INFO);
            }
        }

        return true;
    }
}
