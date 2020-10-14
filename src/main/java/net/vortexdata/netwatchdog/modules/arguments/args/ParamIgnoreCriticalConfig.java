package net.vortexdata.netwatchdog.modules.arguments.args;

import net.vortexdata.netwatchdog.NetWatchdog;

public class ParamIgnoreCriticalConfig extends ParamBase {
    public ParamIgnoreCriticalConfig() {
        super("ignoreCriticalConfig", "iCC");
    }

    @Override
    public boolean runPreparation(String[] args, String calledName, NetWatchdog netWatchdog) {
        netWatchdog.getLogger().warn("App is set to ignore critical configuration errors. This may cause issues during runtime! Do not use this command if you don't know what you are doing!");
        netWatchdog.getConfigRegister().setIgnoreCriticalConfig();
        return true;
    }
}
