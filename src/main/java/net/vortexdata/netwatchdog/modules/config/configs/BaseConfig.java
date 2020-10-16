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

package net.vortexdata.netwatchdog.modules.config.configs;

import net.vortexdata.netwatchdog.modules.config.ConfigStatus;
import org.json.JSONObject;

import java.io.*;
import java.util.Stack;

/**
 * This is the base class for configs.
 *
 * @author          Sandro Kierner
 * @since 0.0.1
 * @version 0.1.1
 */
public abstract class BaseConfig {

    private ConfigStatus configStatus;
    private String path = "main.cfg";
    private final JSONObject defaultValue;
    private JSONObject value;
    private boolean isCritical;

    public BaseConfig(String path, boolean isCritical) {
        this.path = path;
        this.isCritical = isCritical;
        configStatus = ConfigStatus.UNLOADED;
        defaultValue = populateDefaultValue();
    }

    public BaseConfig(String path) {
        this(path, true);
    }

    public boolean load() {
        return load(true);
    }

    public boolean load(boolean createIfNonExistent) {

        // Quickly check if the config actually exists
        File configFile = new File(path);
        if (createIfNonExistent && (!configFile.exists() || configFile.isDirectory()))
            create();

        JSONObject loadedValue = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            StringBuilder configContent = new StringBuilder();
            while (br.ready())
                configContent.append(br.readLine());
            loadedValue = new JSONObject(br.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Run recursive insert task
        // TODO: If any keys were missing, inform user about regneration.


        configStatus = ConfigStatus.LOADED;
        return false;
    }

    private boolean regenerateMissingKeys() {
        return true;
    }

    public boolean create() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path, false));
            bw.write(defaultValue.toString(3));
            bw.flush();
            bw.close();
            return load(false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ConfigStatus getConfigStatus() {
        return configStatus;
    }

    public String getPath() {
        return path;
    }

    public JSONObject getDefaultValue() {
        return defaultValue;
    }

    public JSONObject getValue() {
        return value;
    }

    public abstract Stack<String> checkIntegrity();

    public abstract JSONObject populateDefaultValue();

    public boolean isCritical() {
        return isCritical;
    }

}
