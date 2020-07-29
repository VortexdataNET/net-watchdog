package net.vortexdata.netwatchdog.configs;

import org.json.JSONObject;

import java.io.*;

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
            value = new JSONObject(sb.toString());

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
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
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

    public abstract boolean checkIntegrity();

    public abstract JSONObject populateDefaultValue();

}
