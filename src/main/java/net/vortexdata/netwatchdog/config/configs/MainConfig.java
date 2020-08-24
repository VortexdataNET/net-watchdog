/*
 * NET Watchdog
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

package net.vortexdata.netwatchdog.config.configs;

import org.json.JSONObject;

import java.util.Stack;

/**
 * @author  Sandro Kierner
 * \\\@version 0.0.3
 * \@since 0.0.1
 */
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
