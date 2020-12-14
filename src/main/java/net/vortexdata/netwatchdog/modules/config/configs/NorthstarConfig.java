package net.vortexdata.netwatchdog.modules.config.configs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Stack;

public class NorthstarConfig extends BaseConfig {

    private boolean canNorthstarsBeUsed;
    public static final String CONFIG_PATH = "northstar.conf";

    public NorthstarConfig() {
        super(CONFIG_PATH);
        canNorthstarsBeUsed = true;
    }

    @Override
    public Stack<String> checkIntegrity() {
        Stack<String> errorStack = new Stack<>();

        JSONObject value = getValue();

        if (value.has("availPercentMin")) {
            try {
                int percent = value.getInt("availPercentMin");
                if (percent > 100 || percent < 0) {
                    canNorthstarsBeUsed = false;
                    errorStack.push(percent + " is not a valid percentage.");
                }
            } catch (Exception e) {
                canNorthstarsBeUsed = false;
                errorStack.push("Can not parse availPercentMin value to an integer.");
            }
        } else {
            canNorthstarsBeUsed = false;
            errorStack.add("Minimum availability percentage is not set.");
        }

        if (value.has("threadCount")) {
            if (value.getInt("threadCount") < 1) {
                canNorthstarsBeUsed = false;
                errorStack.add("Value \"threadCount\" is set to an invalid amount. It must be higher than 0.");
            }
        }

        return errorStack;
    }

    @Override
    public JSONObject populateDefaultValue() {
        JSONObject obj = new JSONObject();

        JSONArray northstars = new JSONArray();

        JSONObject northstar1 = new JSONObject();
        northstar1.put("type", "ICMP");
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
        obj.put("threadCount", "4");
        return obj;
    }

    public boolean canNorthstarsBeUsed() {
        return canNorthstarsBeUsed;
    }
}
