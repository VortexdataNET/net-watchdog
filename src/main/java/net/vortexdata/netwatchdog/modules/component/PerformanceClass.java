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

package net.vortexdata.netwatchdog.modules.component;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.utils.RequestMethod;
import net.vortexdata.netwatchdog.utils.RestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class for performance classes.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class PerformanceClass {

    private final String name;
    // 0 = start, 1 = end value
    private final int[] responseTimeRange;
    private int lastRecordedResponseTime;
    private final String contentLookup;
    private final ArrayList<PerformanceClassWebhook> webhooks;
    private final NetWatchdog netWatchdog;

    public PerformanceClass(String name, int[] responseTimeRange, String contentLookup, ArrayList<PerformanceClassWebhook> webhooks, NetWatchdog netWatchdog) {
        this.name = name;
        this.responseTimeRange = responseTimeRange;
        this.contentLookup = contentLookup;
        this.webhooks = webhooks;
        this.netWatchdog = netWatchdog;
    }

    /**
     * Checks if performance class response content lookup has match.
     *
     * @param   responseString    Response string from performance class.
     * @return                    <code>true</code> if match was found;
     *                            <code>false<code> if no match was found.
     */
    public boolean lookupContent(String responseString) {
        if (responseString == null || contentLookup == null)
            return false;
        return responseString.contains(contentLookup);
    }

    /**
     * Runs configured webhooks if applicable.
     */
    public void runWebhooks() {

        if (webhooks == null || webhooks.isEmpty())
            return;

        for (PerformanceClassWebhook pcw : webhooks) {
            try {
                String body = "";
                if (pcw.getBody() != null && !pcw.getBody().isEmpty())
                    body = pcw.getBody();
                HttpsURLConnection hurlc = RestUtils.getPostConnection(netWatchdog.getAppInfo(), RestUtils.getPostBytes(body), pcw.getAddress(), "application/json", pcw.getHeaders());
                if (hurlc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    netWatchdog.getLogger().info("OK from webhook " + pcw.getAddress() + ".");
                } else {
                    netWatchdog.getLogger().info("Got non-ok status return from webhook " + pcw.getAddress() + ". (Expected " + HttpURLConnection.HTTP_OK + ", got " + hurlc.getResponseCode() + ")");
                }
            } catch (SocketTimeoutException e) {
                netWatchdog.getLogger().warn("Connection to webhook " + pcw.getAddress() + " failed.");
            } catch (IOException e) {
                netWatchdog.getLogger().warn("Failed to call webhook for address " + pcw.getAddress() + ": " + e.getMessage());
            }
        }
    }


    public int getLastRecordedResponseTime() {
        return lastRecordedResponseTime;
    }

    public void setLastRecordedResponseTime(int lastRecordedResponseTime) {
        this.lastRecordedResponseTime = lastRecordedResponseTime;
    }

    public String getName() {
        return name;
    }

    public int[] getResponseTimeRange() {
        return responseTimeRange;
    }




    public static ArrayList<PerformanceClass> constructPerformanceClassesFromJSONArray(NetWatchdog netWatchdog, String componentName, JSONArray array) {
        ArrayList<PerformanceClass> export = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            PerformanceClass pc = PerformanceClass.constructPerformanceClassFromJSON(netWatchdog, componentName, array.getJSONObject(i));
            if (pc != null)
                export.add(pc);
        }
        return export;
    }

    public static ArrayList<PerformanceClassWebhook> getPerformanceClassWebhooksFromJSONArray(NetWatchdog netWatchdog, String componentName, String pcName, JSONArray array) {
        ArrayList<PerformanceClassWebhook> performanceClassWebhooks = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (!obj.has("address")) {
                netWatchdog.getLogger().error("Failed to add performance class webhook for performance class " + pcName + " in component "+ componentName + " as its address is not defined.");
                continue;
            }

            HashMap<String, String> headers = new HashMap<String, String>();
            JSONArray headerarray = obj.getJSONArray("headers");
            for (int j = 0; j < headerarray.length(); j++) {
                String[] pair = headerarray.getString(j).split(":");
                if (pair.length != 2) {
                    netWatchdog.getLogger().warn("Skipping addition of webhook header " + headerarray.getString(j) + " as its malformed. Please consult documentation.");
                    continue;
                }
                headers.put(pair[0], pair[1]);
            }

            String body = null;
            try {
                body = obj.getString("body");
            } catch (Exception e) {
                netWatchdog.getLogger().debug("No body parameter found for performance class " + pcName + " in component " + componentName + ".");
            }

            performanceClassWebhooks.add(new PerformanceClassWebhook(
                    obj.getString("address"),
                    headers,
                    body
            ));
        }
        return performanceClassWebhooks;
    }



    public static PerformanceClass constructPerformanceClassFromJSON(NetWatchdog netWatchdog, String componentName, JSONObject obj) {
        if (!obj.has("name")) {
            netWatchdog.getLogger().error("Can not construct performance class for component " + componentName + " as its name is not configured.");
            return null;
        }
        if (!obj.has("responseTimeRange")) {
            netWatchdog.getLogger().error("Can not construct performance class "+obj.get("name")+" for component " + componentName + " as its response time range is not configured.");
            return null;
        }
        if (!obj.has("webhookPosts")) {
            netWatchdog.getLogger().error("Can not construct performance class "+obj.get("name")+" for component " + componentName + " as no webhooks are configured.");
            return null;
        }

        String name = obj.getString("name");
        String[] responseTimeRange = obj.getString("responseTimeRange").split("-");
        int[] responseTimes = new int[2];
        try {
            responseTimes[0] = Integer.parseInt(responseTimeRange[0]);
            responseTimes[1] = Integer.parseInt(responseTimeRange[1]);
        } catch (Exception e) {
            if (responseTimeRange[0].equalsIgnoreCase("timeout")) {
                responseTimes[0] = -1;
                responseTimes[1] = -1;
            } else {
                netWatchdog.getLogger().error("Failed to parse response time range of performance class "+name+" in component " + componentName + ".");
                return null;
            }
        }
        String contentLookup = null;
        try {
            contentLookup = obj.getString("contentLookup");
        } catch (Exception e) {
            netWatchdog.getLogger().debug("Content lookup header not found for component " + componentName + ".");
        }

        ArrayList<PerformanceClassWebhook> webhooks = getPerformanceClassWebhooksFromJSONArray(netWatchdog, componentName, name, obj.getJSONArray("webhookPosts"));

        return new PerformanceClass(name, responseTimes, contentLookup, webhooks, netWatchdog);

    }
}
