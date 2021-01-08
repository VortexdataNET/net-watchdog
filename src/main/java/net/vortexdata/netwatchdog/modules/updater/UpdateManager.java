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

package net.vortexdata.netwatchdog.modules.updater;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import net.vortexdata.netwatchdog.modules.console.logging.Log;
import net.vortexdata.netwatchdog.utils.GithubAPIUtils;
import net.vortexdata.netwatchdog.utils.RestUtils;
import net.vortexdata.netwatchdog.utils.VersionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class UpdateManager {

    public static final String API_URL = "https://api.github.com/repos/VortexdataNET/net-watchdog";
    public static final String SYS_PATH = "dnldmng";
    public static final String UPDATE_AVAILABLE_MESSAGE = "There is a new update available for download! Please use the command 'app upgrade' to get more information.";
    private static int UPDATE_AVAILABLE_PAUSE = 5000;
    private NetWatchdog netWatchdog;

    public UpdateManager(NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
    }

    /**
     * Downloads release with matching release tag.
     * @param tag       {@link String} specifying the release to download.
     * @return          <code>true</code> if release has been downloaded successfully;
     *                  <code>false</code> if download failed.
     */
    public boolean downloadRelease(String tag) {
        Log.debug("Trying to download release...");
        File sysDirectory = new File("sys//"+SYS_PATH+"//releases");
        if (!sysDirectory.exists() || !sysDirectory.isDirectory())
            if (!sysDirectory.mkdirs()) {
                Log.error("Failed to create download manager system directory.");
                return false;
            }

        JSONObject jsonObject = getReleaseInfo(tag);
        if (jsonObject == null) {
            Log.error("Failed to fetch release info ("+tag+").");
            return false;
        }


        JSONObject releaseInfo = GithubAPIUtils.getJarAssetInfo(jsonObject.getJSONArray("assets"));
        if (releaseInfo == null) {
            Log.error("Got invalid release info from API endpoint.");
            return false;
        }

        try (BufferedInputStream in = new BufferedInputStream(new URL(releaseInfo.getString("browser_download_url")).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(netWatchdog.getSysPath() + SYS_PATH + "//releases//"+tag+".jar", false)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Log.error("Download of release asset failed: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Fetches release info by tag from GitHub release API.
     *
     * @param tag   {@link String} specifying release tag.
     * @return      {@link JSONObject} containing release info.
     */
    public JSONObject getReleaseInfo(String tag) {
        try {
            HttpsURLConnection hurlc = RestUtils.getGetConnection(API_URL + "/releases/tags/"+tag);
            if (hurlc.getResponseCode() != HttpsURLConnection.HTTP_OK)
                return null;

            String response = RestUtils.readResponseStream(new BufferedReader(
                    new InputStreamReader(hurlc.getInputStream())));

            return new JSONObject(response);
        } catch (Exception e) {
            Log.error("Failed to fetch release "+tag+": " + e.getMessage());
            return null;
        }
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

    /**
     * Checks if release tag is available.
     * @param tag   Release tag.
     * @return      <code>true</code> if it's available.
     *              <code>false</code> if it's not.
     */
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

    /**
     * Looks for and returns latest releases version tag.
     * @return      {@link String} representing latest version tag.
     */
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

    /**
     * Fetches available releases from GitHub API.
     * @return      {@link JSONArray} containing all available releases.
     */
    public JSONArray fetchAvailableReleases() {
        try {
            HttpsURLConnection hurlc = RestUtils.getGetConnection(API_URL + "/git/refs/tags");
            if (hurlc.getResponseCode() != HttpsURLConnection.HTTP_OK)
                return null;

            String response = RestUtils.readResponseStream(new BufferedReader(
                    new InputStreamReader(hurlc.getInputStream())));

            return new JSONArray(response);
        } catch (Exception e) {
            Log.debug("Failed to fetch available releases: " + e.getMessage());
            return null;
        }
    }

    public static String getDownloadedReleasePath(String tag) {
        return NetWatchdog.getSysPath() + SYS_PATH + "//releases//"+tag+".jar";
    }

}
