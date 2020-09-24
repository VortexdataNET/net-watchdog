package net.vortexdata.netwatchdog.modules.config.configs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Stack;

public class NorthstarConfig extends BaseConfig {

    private boolean canNorthstarsBeUsed;
    public static final String CONFIG_PATH = "northstar.conf";

    public NorthstarConfig() {
        super(CONFIG_PATH);
        canNorthstarsBeUsed = false;
    }

    @Override
    public Stack<String> checkIntegrity() {
        Stack<String> errorStack = new Stack<>();

        JSONObject value = getValue();

        if (!value.has("availPercentMin")) {
            errorStack.add("Minimum availability percentage is not set.");
            canNorthstarsBeUsed = false;
        }


        return errorStack;
    }

    @Override
    public JSONObject populateDefaultValue() {
        JSONObject obj = new JSONObject();

        JSONArray northstars = new JSONArray();

        JSONObject northstar1 = new JSONObject();
        northstar1.put("type", "ICMP");
        northstar1.put("samples", "1");
        northstar1.put("address", "1.1.1.1");

        JSONObject northstar2 = new JSONObject();
        northstar2.put("type", "SOCKET");
        northstar2.put("timeout", "6000");
        northstar2.put("address", "8.8.8.8");
        northstar2.put("port", "80");

        northstars.put(northstar1);
        northstars.put(northstar2);

        obj.put("northstars", northstars);
        obj.put("availPercentMin", "100");
        return obj;
    }

}
