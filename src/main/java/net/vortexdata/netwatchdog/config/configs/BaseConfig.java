package net.vortexdata.netwatchdog.config.configs;

import net.vortexdata.netwatchdog.config.ConfigStatus;
import org.json.JSONObject;

import java.io.*;
import java.util.Stack;

/**
 * This is the base class for configs.
 *
 * @author          Sandro Kierner
 * @since           0.0.0
 * @version         0.0.0
 */
public abstract class BaseConfig {

    private ConfigStatus configStatus;
    private String path = "main.cfg";
    private JSONObject defaultValue;
    private JSONObject value;

    public BaseConfig(String path) {
        this.path = path;
        configStatus = ConfigStatus.UNLOADED;
        defaultValue = populateDefaultValue();
    }

    public boolean load() {
        return load(true);
    }

    public boolean load(boolean createIfNonExistent) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                sb.append(br.readLine());
            }
            try {
                value = new JSONObject(sb.toString());
            } catch (Exception e) {
                create();
            }


            configStatus = ConfigStatus.LOADED;
        } catch (FileNotFoundException e) {
            if (createIfNonExistent)
                create();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return false;
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

}
