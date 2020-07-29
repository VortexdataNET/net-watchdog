package net.vortexdata.netwatchdog.console.commands;

import org.slf4j.Logger;

public abstract class BaseCommand {

    private String name;
    private Logger logger;

    public BaseCommand(Logger logger, String name) {
        this.logger = logger;
        this.name = name;
    }

    public abstract void call(String[] args);

    public String getName() {
        return name;
    }

    public Logger getLogger() {
        return logger;
    }
}
