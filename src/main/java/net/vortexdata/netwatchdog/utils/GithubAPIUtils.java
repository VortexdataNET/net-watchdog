package net.vortexdata.netwatchdog.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class GithubAPIUtils {

    public static JSONObject getJarAssetInfo(JSONObject release) {
        return getJarAssetInfo(release.getJSONArray("assets"));
    }

    public static JSONObject getJarAssetInfo(JSONArray assets) {
        for (int i = 0; i < assets.length(); i++) {
            if (assets.getJSONObject(i).getString("name").equalsIgnoreCase("net-watchdog.jar"))
                return assets.getJSONObject(i);
        }
        return null;
    }

}
