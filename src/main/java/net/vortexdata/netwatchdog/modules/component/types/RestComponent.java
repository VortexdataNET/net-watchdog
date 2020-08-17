package net.vortexdata.netwatchdog.modules.component.types;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.component.FallbackPerformanceClass;
import net.vortexdata.netwatchdog.modules.component.PerformanceClass;
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

    private final HashMap<String, String> headers;
    private final String body;
    private final RequestMethod requestMethod;

    public RestComponent(String address, String name, String filename, ArrayList<PerformanceClass> performanceClasses, HashMap<String, String> headers, String body, RequestMethod requestMethod) {
        super(address, name, filename, performanceClasses);
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
            int responseTime = (int) (System.currentTimeMillis() - start);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String response = RestUtils.readResponseStream(new BufferedReader(
                        new InputStreamReader(hurlc.getInputStream())));

                for (PerformanceClass pc : performanceClasses)
                    if (pc.lookupContent(response))
                        return pc;

                // Fallback on response time base evaluation
                return getPerformanceClassByResponseTime(responseTime);
            } else {
                // TODO: Give more user agency about what happens if non-ok status is returned
                return getPerformanceClassByResponseTime(-1);
            }
        } catch (IOException e) {
            return new FallbackPerformanceClass(-1, e.getMessage());
        }
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
        method = method.toUpperCase();
        if (!method.equalsIgnoreCase("POST") && !method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("PUT")) {
            netWatchdog.getLogger().error("Failed to construct rest component as the specified request time is not supported.");
            return null;
        }
        RequestMethod rmethod = RequestMethod.valueOf(method);
        String name = obj.getString("name");
        String address = obj.getString("address");
        String body = "";
        try {
            body = obj.getString("body");
        } catch (JSONException e) {
            netWatchdog.getLogger().debug("No body found for component " + name + ".");
        }
        String filename = obj.getString("filename");
        ArrayList<PerformanceClass> pcs = ComponentManager.constructPerformanceClassesFromJSONArray(netWatchdog, name, obj.getJSONArray("performanceClasses"));

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
                address,
                name,
                filename,
                pcs,
                headers,
                body,
                rmethod
        );
    }

}
