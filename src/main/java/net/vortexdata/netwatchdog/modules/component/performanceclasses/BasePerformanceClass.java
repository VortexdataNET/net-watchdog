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

package net.vortexdata.netwatchdog.modules.component.performanceclasses;

import net.vortexdata.netwatchdog.exceptions.InvalidPerformanceClassJSONException;
import net.vortexdata.netwatchdog.exceptions.InvalidWebhookJSONException;
import net.vortexdata.netwatchdog.modules.console.logging.Log;
import net.vortexdata.netwatchdog.utils.RestUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class for performance classes.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class BasePerformanceClass {

    private final String name;
    private final ArrayList<PerformanceClassWebhook> webhooks;

    // 0 = start, 1 = end value
    //private final int[] responseTimeRange;
    //private int lastRecordedResponseTime;
    //private final String contentLookup;


    /*
    public BasePerformanceClass(String name, int[] responseTimeRange, String contentLookup, ArrayList<PerformanceClassWebhook> webhooks) {
        this.name = name;
        this.responseTimeRange = responseTimeRange;
        this.contentLookup = contentLookup;
        this.webhooks = webhooks;
    }
   */




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

                HttpsURLConnection hurlc = RestUtils.getPostConnection(
                        RestUtils.getPostBytes(body),
                        pcw.getAddress(),
                        "application/json",
                        pcw.getHeaders()
                );

                if (hurlc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.info("OK from webhook " + pcw.getAddress() + ".");
                } else {
                    Log.info("NON-OK status from webhook " + pcw.getAddress() + ". (" + hurlc.getResponseCode() + ")");
                }
            } catch (SocketTimeoutException e) {
                Log.warn("Connection to webhook " + pcw.getAddress() + " failed.");
            } catch (IOException e) {
                Log.warn("Failed to call webhook for address " + pcw.getAddress() + ": " + e.getMessage());
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




    public static ArrayList<BasePerformanceClass> constructPerformanceClassesFromJSONArray(JSONArray array) {
        ArrayList<BasePerformanceClass> export = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                BasePerformanceClass pc = BasePerformanceClass.constructPerformanceClassFromJSON(array.getJSONObject(i));
                export.add(pc);
            } catch (InvalidPerformanceClassJSONException e) {
                e.printStackTrace();
            }
        }
        return export;
    }

    public static ArrayList<PerformanceClassWebhook> getPerformanceClassWebhooksFromJSONArray(JSONArray array) {
        ArrayList<PerformanceClassWebhook> performanceClassWebhooks = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                performanceClassWebhooks.add(getPerformanceClassWebhookFromJSON(array.getJSONObject(i)));
            } catch (InvalidWebhookJSONException e) {
                e.printStackTrace();
            }
        }
        return performanceClassWebhooks;
    }

    public static PerformanceClassWebhook getPerformanceClassWebhookFromJSON(JSONObject obj) throws InvalidWebhookJSONException {

        if (!obj.has("address"))
            throw new InvalidWebhookJSONException("Address key is not set.");

        // Determine headers
        HashMap<String, String> headers = new HashMap<>();
        if (!obj.has("headers")) {
            JSONArray headerarray = obj.getJSONArray("headers");
            for (int j = 0; j < headerarray.length(); j++) {
                String[] pair = headerarray.getString(j).split(":");
                if (pair.length != 2) {
                    Log.warn("Skipping addition of webhook header " + headerarray.getString(j) + " as its malformed. Please consult documentation.");
                    continue;
                }
                headers.put(pair[0], pair[1]);
            }
        }

        String body = "";
        if (obj.has("body"))
            body = obj.getString("body");

        return new PerformanceClassWebhook(
            obj.getString("address"),
            headers,
            body
        );
    }



    public static BasePerformanceClass constructPerformanceClassFromJSON(JSONObject obj) throws InvalidPerformanceClassJSONException {
        if (!obj.has("responseTimeRange")) {
            throw new InvalidPerformanceClassJSONException("Response time range is not set.");
        }
        if (!obj.has("webhookPosts")) {
            throw new InvalidPerformanceClassJSONException("No webhook posts are set.");
        }
        if (!obj.has("name")) {
            throw new InvalidPerformanceClassJSONException("No name is set.");
        }

        String name = obj.getString("name");

        // Determine response time range
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
                throw new InvalidPerformanceClassJSONException("Failed to parse response time range.");
            }
        }

        String contentLookup = null;
        if (obj.has("contentLookup"))
            contentLookup = obj.getString("contentLookup");



        ArrayList<PerformanceClassWebhook> webhooks = getPerformanceClassWebhooksFromJSONArray(obj.getJSONArray("webhookPosts"));

        return new BasePerformanceClass(name, responseTimes, contentLookup, webhooks);

    }
}
