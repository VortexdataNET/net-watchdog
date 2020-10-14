package net.vortexdata.netwatchdog.modules.updater;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import net.vortexdata.netwatchdog.utils.RestUtils;
import net.vortexdata.netwatchdog.utils.VersionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UpdateManager {

    public static final String UPDATE_AVAILABLE_MESSAGE = "There is a new update available for download! Please use the command 'app upgrade' to get more information.";
    private static int UPDATE_AVAILABLE_PAUSE = 5000;
    private NetWatchdog netWatchdog;

    public UpdateManager(NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
    }

    public void promptUpdateAvailable() {
        String delimiter = "-------------------------------------------";
        String message = delimiter + "\n" + UPDATE_AVAILABLE_MESSAGE + "\n" + delimiter;
        CLI.print(message);
        try {
            Thread.sleep(UPDATE_AVAILABLE_PAUSE);
        } catch (Exception e) {
            // Ignore as it doesn't matter
        }
    }

    public boolean isTagAvailable(String tag) {
        JSONArray array = fetchAvailableReleases();
        for (int i = 0; i < array.length(); i++) {
            if (getVersionTagFromRelease(array.getJSONObject(i)).equals(tag))
                return true;
        }
        return false;
    }

    public boolean areUpdatesAvailable() {
        JSONArray array = fetchAvailableReleases();
        if (array == null) return false;
        String[] ref = ((JSONObject) array.get(array.length()-1)).getString("ref").split("/");
        return (VersionUtils.compareVersionTags(ref[ref.length-1], netWatchdog.getAppInfo().getVersionName()) == 1);
    }

    public String getLatestVersionTag() {
        JSONArray array = fetchAvailableReleases();
        if (array != null) {
            return getVersionTagFromRelease(array.getJSONObject(array.length()-1));
        }
        return null;
    }

    private String getVersionTagFromRelease(JSONObject release) {
        String[] ref = release.getString("ref").split("/");
        return ref[ref.length-1];
    }

    public JSONArray fetchAvailableReleases() {
        try {
            HttpsURLConnection hurlc = RestUtils.getGetConnection(netWatchdog.getAppInfo(), "https://api.github.com/repos/VortexdataNET/net-watchdog/git/refs/tags");
            if (hurlc.getResponseCode() != HttpsURLConnection.HTTP_OK)
                return null;

            String response = RestUtils.readResponseStream(new BufferedReader(
                    new InputStreamReader(hurlc.getInputStream())));

            return new JSONArray(response);
        } catch (Exception e) {
            netWatchdog.getLogger().debug("Failed to fetch available releases: " + e.getMessage());
            return null;
        }
    }

}
