package net.vortexdata.netwatchdog.modules.arguments.args;

import net.vortexdata.netwatchdog.NetWatchdog;

public abstract class ParamBase {

    protected String fullName;
    protected String shortName;

    public ParamBase(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public abstract boolean runPreparation(String[] args, String calledName, NetWatchdog netWatchdog);

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }
}
