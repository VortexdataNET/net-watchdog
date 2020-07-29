package net.vortexdata.netwatchdog.config.configs;

import net.vortexdata.netwatchdog.config.configs.BaseConfig;

import java.util.Stack;

public abstract class TargetConfig extends BaseConfig {

    public TargetConfig(String path) {
        super(path);
    }

    public Stack<String> checkIntegrity() {



        return null;
    }

}
