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

package net.vortexdata.netwatchdog.modules.component.types;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.component.FallbackPerformanceClass;
import net.vortexdata.netwatchdog.modules.component.PerformanceClass;
import net.vortexdata.netwatchdog.utils.AppInfo;
import net.vortexdata.netwatchdog.utils.RequestMethod;
import net.vortexdata.netwatchdog.utils.RestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * RestComponent used to check websites and REST APIs.
 *
 * @author          Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class RestComponent extends BaseComponent {

    private final HashMap<String, String> headers;
    private final String body;
    private final RequestMethod requestMethod;
    private final AppInfo appInfo;

    public RestComponent(String filename, AppInfo appInfo, String address, ArrayList<PerformanceClass> performanceClasses, boolean cachePerformanceClass, HashMap<String, String> headers, String body, RequestMethod requestMethod) {
        super(filename, address, performanceClasses, cachePerformanceClass);
        this.appInfo = appInfo;
        this.headers = headers;
        this.body = body;
        this.requestMethod = requestMethod;
    }

    @Override
    protected PerformanceClass runPerformanceCheck() {

        HttpsURLConnection hurlc = null;
        try {
            if (requestMethod == RequestMethod.POST)
                hurlc = post(body, address, headers);
            else if (requestMethod == RequestMethod.GET) {
                hurlc = get(address, headers);
            }
        } catch (Exception e) {
            return new FallbackPerformanceClass(-1, e.getMessage());
        }


        // Get server response and check which performance class has matching content lookup
        // Content lookup overrules ping check
        try {
            long start = System.currentTimeMillis();
            int responseCode = hurlc.getResponseCode();
            int responseTime = (int) (System.currentTimeMillis() - start);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String response = RestUtils.readResponseStream(new BufferedReader(
                        new InputStreamReader(hurlc.getInputStream())));

                for (PerformanceClass pc : performanceClasses)
                    if (pc.lookupContent(response)) {
                        pc.setLastRecordedResponseTime(responseTime);
                        return pc;
                    }


                // Fallback on response time base evaluation
                PerformanceClass pc = getPerformanceClassByResponseTime(responseTime);
                pc.setLastRecordedResponseTime(responseTime);
                return pc;
            } else {
                // TODO: Give more user agency about what happens if non-ok status is returned
                PerformanceClass pc = getPerformanceClassByResponseTime(-1);
                pc.setLastRecordedResponseTime(-1);
                return pc;
            }
        } catch (IOException e) {
            return new FallbackPerformanceClass(-1, e.getMessage());
        }
    }

    // TODO: Add parameter support
    private HttpsURLConnection get(String url, HashMap<String, String> extraHeaders) {
        try {
            return RestUtils.getGetConnection(appInfo, address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpsURLConnection post(String body, String url, HashMap<String, String> extraHeaders) {
        if (url == null || url.isEmpty() || body == null || body.isEmpty())
            return null;
        try {
            return RestUtils.getPostConnection(appInfo, RestUtils.getPostBytes(body), url, "application/json", extraHeaders);
        } catch (JSONException | IOException e) {
            return null;
        }
    }

    public static RestComponent getRestComponentFromJSON(AppInfo appInfo, JSONObject obj, NetWatchdog netWatchdog) {
        String method = obj.getString("method");
        method = method.toUpperCase();
        if (!method.equalsIgnoreCase("POST") && !method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("PUT")) {
            netWatchdog.getLogger().error("Failed to construct rest component as the specified request time is not supported.");
            return null;
        }
        RequestMethod rmethod = RequestMethod.valueOf(method);
        String name = obj.getString("name");
        String address = obj.getString("address");
        boolean cachePerformanceClass = true;
        try {
            if (obj.getString("cacheLastResult").equalsIgnoreCase("false"))
                cachePerformanceClass = false;
        } catch (Exception e) {
            netWatchdog.getLogger().debug("Couldn't find cacheLastResult key, falling back to true.");
        }
        String body = "";
        try {
            body = obj.getString("body");
        } catch (JSONException e) {
            netWatchdog.getLogger().debug("No body found for component " + name + ".");
        }
        String filename = obj.getString("filename");
        ArrayList<PerformanceClass> pcs = PerformanceClass.constructPerformanceClassesFromJSONArray(netWatchdog, name, obj.getJSONArray("performanceClasses"));

        HashMap<String, String> headers = new HashMap<>();
        try {
            JSONArray headersJSON = obj.getJSONArray("headers");
            for (int i = 0; i < headersJSON.length(); i++) {
                String[] parts = headersJSON.getString(i).split(":");
                if (parts.length == 2) {
                    headers.put(parts[0], parts[1]);
                } else {
                    netWatchdog.getLogger().warn("Skipping addition of request header " + headersJSON.getString(i) + " as it's malformed. Please consult documentation.");
                }
            }
        } catch (JSONException e) {
            netWatchdog.getLogger().debug("No headers for component " + name + " found.");
        }

        return new RestComponent(
                filename,
                appInfo,
                address,
                pcs,
                cachePerformanceClass,
                headers,
                body,
                rmethod
        );
    }

}
