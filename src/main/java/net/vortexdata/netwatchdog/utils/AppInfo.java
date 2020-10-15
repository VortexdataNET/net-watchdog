package net.vortexdata.netwatchdog.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Wrapper class holding project information such as version tag, build date, etc.
 *
 * @author          Sandro Kierner
 * @since 0.1.0
 * @version 0.1.1
 */
public class AppInfo {

    private JSONObject values;
    private Platform platform;

    public AppInfo() {
        values = new JSONObject();
        platform = Platform.getPlatformFromString(System.getProperty("os.name"));
    }

    public boolean loadProjectConfig() {

        StringBuffer sb = new StringBuffer();
        BufferedReader headBr = null;
        try {
            InputStream headIs = getClass().getResourceAsStream("/project.json");
            headBr = new BufferedReader(new InputStreamReader(headIs));
            while (headBr.ready()) {
                sb.append(headBr.readLine());
            }
        } catch (Exception e) {
            return false;
        }

        values = new JSONObject(sb.toString());
        return true;
    }

    public JSONObject getValues() {
        return values;
    }

    public String getVersionName() {
        if (values.has("versionName"))
            return values.getString("versionName");
        else
            return "0.0.0";
    }

    public String getArch() {
        return System.getProperty("os.arch");
    }

    public Platform getPlatform() {
        return platform;
    }
}
