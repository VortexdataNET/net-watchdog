package net.vortexdata.netwatchdog.config;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.config.configs.MainConfig;
import net.vortexdata.netwatchdog.config.configs.TargetConfig;

import java.util.ArrayList;
import java.util.Stack;

public class ConfigRegister {

    private NetWatchdog netWatchdog;
    private MainConfig mainConfig;

    public ConfigRegister(NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
        mainConfig = new MainConfig();
        loadAll();
    }

    public boolean loadAll() {
        loadMainConfig();
        return true;
    }

    public boolean loadMainConfig() {
        mainConfig.load();
        Stack<String> errors = mainConfig.checkIntegrity();
        if (mainConfig.checkIntegrity() != null && !mainConfig.checkIntegrity().isEmpty()) {
            netWatchdog.getLogger().error("Integrity check for main config failed, logging error stack:");
            for (String s : errors) {
                netWatchdog.getLogger().error(s);
            }
            netWatchdog.getLogger().info("Check check your main configuration for the above mentions error(s) and try again.");
            return false;
        }
        return true;
    }

    public NetWatchdog getNetWatchdog() {
        return netWatchdog;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }
}
