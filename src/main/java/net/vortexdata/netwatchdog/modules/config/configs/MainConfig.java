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

import org.json.JSONObject;

import java.util.Stack;

/**
 * @author  Sandro Kierner
 * @version 0.2.0
 * @since 0.0.1
 */
public class MainConfig extends BaseConfig {

    public static final String CONFIG_PATH = "main.conf";

    public MainConfig() {
        super(CONFIG_PATH);
    }

    @Override
    public Stack<String> checkIntegrity() {

        Stack<String> errorStack = new Stack<>();

        JSONObject value = getValue();
        try {
            value.getBoolean("enableNorthstars");
        } catch (Exception e) {
            errorStack.push("Invalid value for key 'enableNorthstars', value must be a boolean.");
        }

        if (value.has("pollDelay")) {
            String pollDelayS = value.getString("pollDelay");
            int pollDelay = -1;
            try {
                if (Integer.parseInt(pollDelayS) < 0)
                    errorStack.push("The 'pollDelay' value must be higher than 0.");
            } catch (Exception e) {
                errorStack.push("Failed to parse 'pollDelay' to an integer.");
            }
        }

        return errorStack;
    }

    @Override
    public JSONObject populateDefaultValue() {
        JSONObject obj = new JSONObject();
        obj.put("enableNorthstars", "true");
        obj.put("pollDelay", "30");
        obj.put("threadCount", "4");
        obj.put("threadTerminationThreshold", "60");
        return obj;
    }

}
