package net.vortexdata.netwatchdog.modules.config.configs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Stack;

public class NorthstarConfig extends BaseConfig {

    public NorthstarConfig() {
        super("northstar.conf");
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
        JSONArray addressArray = new JSONArray();
        addressArray.put("1.1.1.1");
        addressArray.put("8.8.8.8");
        addressArray.put("8.8.4.4");
        obj.put("addresses", addressArray);
        obj.put("availPercentMin", "100");
        return obj;
    }

}
