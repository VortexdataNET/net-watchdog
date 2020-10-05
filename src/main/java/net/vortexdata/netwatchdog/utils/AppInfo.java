package net.vortexdata.netwatchdog.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Wrapper class holding project information such as version tag, build date, etc.
 *
 * @author          Sandro Kierner
 * @since 0.0.0
 * @version 0.0.0
 */
public class AppInfo {

    private JSONObject values;

    public AppInfo() {
        values = new JSONObject();
    }

    public boolean load() {

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
}