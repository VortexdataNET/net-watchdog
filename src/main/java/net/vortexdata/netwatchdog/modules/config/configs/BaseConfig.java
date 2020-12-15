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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * This is the base class for configs.
 *
 * @author          Sandro Kierner
 * @since 0.0.1
 * @version 0.3.0
 */
public abstract class BaseConfig {

    private ConfigStatus configStatus;
    private String path = "main.cfg";
    private final JSONObject defaultValue;
    private JSONObject value;
    private boolean isCritical;
    private boolean hasBeenUpdated;

    public BaseConfig(String path, boolean isCritical) {
        this.path = path;
        this.isCritical = isCritical;
        configStatus = ConfigStatus.UNLOADED;
        defaultValue = populateDefaultValue();
        hasBeenUpdated = false;
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
            br.close();

            loadedValue = new JSONObject(configContent.toString());

            JSONObject defaultValue = getDefaultValue();

            HashMap<String, String> keyMap = new HashMap<>();

            indexJsonObject(defaultValue, "", keyMap);

            boolean updated = regenerateMissingKeys(keyMap, loadedValue);

            if (updated) {
                create(loadedValue);
                hasBeenUpdated = true;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        value = loadedValue;

        configStatus = ConfigStatus.LOADED;
        return false;
    }

    private void indexJsonObject(JSONObject jsonObject, String path, HashMap<String, String> outputMap) {

        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                JSONObject jsonValue = (JSONObject) value;
                indexJsonObject(jsonValue, path+".", outputMap);
            } else if (value instanceof JSONArray) {
                outputMap.put(path + "." + key, "[JSONArray]");
            } else if (value instanceof String) {
                outputMap.put(path + "." + key, (String) value);
            }
        }
    }

    private boolean regenerateMissingKeys(HashMap<String, String> keyValueMap, JSONObject jsonObject) {
        boolean updatedConfig = false;
        for (String currentPath : keyValueMap.keySet()) {
            JSONObject currentJsonObject = jsonObject;
            String[] path = currentPath.split("\\.");
            for (int i = 0; i < path.length; i++) {
                String pathSegment = path[i];
                if (pathSegment.length() < 1) continue;

                boolean isLast = path.length - i == 1;

                Object value = null;
                if(currentJsonObject.has(pathSegment))
                    value = currentJsonObject.get(pathSegment);

                if (isLast && value == null) {
                    String defaultValue = keyValueMap.get(currentPath);

                    if (defaultValue.equals("[JSONArray]")) {
                        currentJsonObject.put(pathSegment, new JSONArray());
                    } else {
                        currentJsonObject.put(pathSegment, defaultValue);
                    }

                    updatedConfig = true;
                    continue;
                }

                if (!isLast && value == null) {
                    value = new JSONObject();
                    currentJsonObject.put(pathSegment, value);
                    updatedConfig = true;
                    continue;
                }

                if (!isLast && value instanceof JSONObject) {
                    currentJsonObject = (JSONObject) value;
                    updatedConfig = true;
                }
            }
        }

        return updatedConfig;
    }

    public boolean create() {
        return create(defaultValue);
    }

    public boolean create(JSONObject newObj) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path, false));
            bw.write(newObj.toString(3));
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

    public boolean hasBeenUpdated() {
        return hasBeenUpdated;
    }

}
