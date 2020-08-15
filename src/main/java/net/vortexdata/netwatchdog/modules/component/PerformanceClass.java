package net.vortexdata.netwatchdog.modules.component;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.utils.RequestMethod;
import net.vortexdata.netwatchdog.utils.RestUtils;
import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.Buffer;
import java.util.ArrayList;

public class PerformanceClass {

    private String name;
    // 0 = start, 1 = end value
    private int[] responseTimeRange;
    private int lastRecordedResponseTime;
    private String contentLookup;
    private ArrayList<PerformanceClassWebhook> webhooks;
    private NetWatchdog netWatchdog;

    public PerformanceClass(String name, int[] responseTimeRange, String contentLookup, ArrayList<PerformanceClassWebhook> webhooks, NetWatchdog netWatchdog) {
        this.name = name;
        this.responseTimeRange = responseTimeRange;
        this.contentLookup = contentLookup;
        this.webhooks = webhooks;
        this.netWatchdog = netWatchdog;
    }

    public boolean lookupContent(String string) {
        if (string == null || contentLookup == null)
            return false;
        return string.contains(contentLookup);
    }

    public void runWebhooks() {

        if (webhooks == null || webhooks.isEmpty())
            return;

        for (PerformanceClassWebhook pcw : webhooks) {
            try {
                String body = "";
                if (pcw.getBody() != null && !pcw.getBody().isEmpty())
                    body = pcw.getBody();
                HttpsURLConnection hurlc = RestUtils.getPostConnection(RestUtils.getPostBytes(body), pcw.getAddress(), "application/json", pcw.getHeaders());
                if (hurlc.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    netWatchdog.getLogger().info("OK from webook " + pcw.getAddress() + ".");
                } else {
                    netWatchdog.getLogger().error("Got non-ok status return from webhook " + pcw.getAddress() + ". (Expected " + HttpURLConnection.HTTP_OK + ", got " + hurlc.getResponseCode() + ")");
                }
            } catch (SocketTimeoutException e) {
                netWatchdog.getLogger().error("Connection to webhook " + pcw.getAddress() + " failed.");
            } catch (IOException e) {
                netWatchdog.getLogger().error("Failed to call webhook for address " + pcw.getAddress() + ": " + e.getMessage());
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
}
