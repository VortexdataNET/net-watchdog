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
import java.util.ArrayList;
import java.util.HashMap;

public class RestComponent extends BaseComponent {

    private HashMap<String, String> headers;
    private String body;
    private RequestMethod requestMethod;

    public RestComponent(String address, String name, ArrayList<PerformanceClass> performanceClasses, HashMap<String, String> headers, String body, RequestMethod requestMethod) {
        super(address, name, performanceClasses);
        this.headers = headers;
        this.body = body;
        this.requestMethod = requestMethod;
    }

    @Override
    public PerformanceClass runPerformanceCheck() {

        HttpsURLConnection hurlc = null;
        if (requestMethod == RequestMethod.POST)
            hurlc = post(body, address, headers);
        else if (requestMethod == RequestMethod.GET) {
            hurlc = get(address, headers);
        }

        // Get server response and check which performance class has matching content lookup
        // Content lookup overrules ping check
        try {
            long start = System.currentTimeMillis();
            int responseCode = hurlc.getResponseCode();
            long responseTime = System.currentTimeMillis() - start;
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String response = RestUtils.readResponseStream(new BufferedReader(
                        new InputStreamReader(hurlc.getInputStream())));
                for (PerformanceClass pc : performanceClasses)
                    if (pc.lookupContent(response))
                        return pc;

                // Fallback on response time base evaluation
                return getPerformanceClassByResponseTime(responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: Add parameter support
    private HttpsURLConnection get(String url, HashMap<String, String> extraHeaders) {
        try {
            return RestUtils.getGetConnection(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpsURLConnection post(String body, String url, HashMap<String, String> extraHeaders) {
        if (url == null || url.isEmpty() || body == null || body.isEmpty())
            return null;
        try {
            return RestUtils.getPostConnection(RestUtils.getPostBytes(body), url, "application/json", extraHeaders);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RestComponent getRestComponentFromJSON(JSONObject obj, NetWatchdog netWatchdog) {
        String method = obj.getString("method");
        if (!method.equalsIgnoreCase("POST") && !method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("PUT")) {
            netWatchdog.getLogger().error("Failed to construct rest component as the specified request time is not supported.");
            return null;
        }
        String name = obj.getString("name");
        String address = obj.getString("address");
        JSONArray performanceClassesJSON = obj.getJSONArray("performanceClasses");
    }

}
