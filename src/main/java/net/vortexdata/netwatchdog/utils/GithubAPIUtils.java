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

package net.vortexdata.netwatchdog.utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utils class used to fetch project specific
 * information via the GitHub API.
 *
 * @author Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class GithubAPIUtils {

    /**
     * Gets <code>net-watchdog.jar</code> asset information {@link JSONObject}
     * from GitHub release {@link JSONObject}.
     *
     * @param   release     GitHub Release {@link JSONObject} (e.g. obtained from GitHub release API).
     * @return              Asset information {@link JSONObject} matching <code>net-watchdog.jar</code> file name.
     */
    public static JSONObject getJarAssetInfo(JSONObject release) {
        return getJarAssetInfo(release.getJSONArray("assets"));
    }

    /**
     * Gets <code>net-watchdog.jar</code> asset information {@link JSONObject}
     * from {@link JSONArray} asset array.
     *
     * @param   assets  Asset {@link JSONArray} (e.g. obtained from GitHub release API).
     * @return          Asset information {@link JSONObject} matching <code>net-watchdog.jar</code> file name.
     */
    public static JSONObject getJarAssetInfo(JSONArray assets) {
        for (int i = 0; i < assets.length(); i++) {
            if (assets.getJSONObject(i).getString("name").equalsIgnoreCase("net-watchdog.jar"))
                return assets.getJSONObject(i);
        }
        return null;
    }

}
