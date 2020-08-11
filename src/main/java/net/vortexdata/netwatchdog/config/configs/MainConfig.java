package net.vortexdata.netwatchdog.config.configs;

import org.json.JSONObject;

import java.util.Stack;

public class MainConfig extends BaseConfig {

    public MainConfig() {
        super("main.conf");
    }

    @Override
    public Stack<String> checkIntegrity() {

        Stack<String> errorStack = new Stack<>();

        JSONObject value = getValue();
        try {
            value.getBoolean("enabled");
        } catch (Exception e) {
            errorStack.push("Invalid value for key 'enabled', value must be a boolean.");
        }

        return errorStack;
    }

    @Override
    public JSONObject populateDefaultValue() {
        JSONObject obj = new JSONObject();
        obj.put("enabled", "true");
        obj.put("pollRate", "30");
        return obj;
    }

}
