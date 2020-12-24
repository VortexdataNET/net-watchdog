/*
 * MIT License
 *
 * Copyright (c) 2020 VortexdataNET
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
