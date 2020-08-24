/*
 * NET Watchdog
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
import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.nio.Buffer;
import java.util.ArrayList;

/**
 * Base class for performance classes.
 *
 * @author  Sandro Kierner
 * \@since 0.0.1
 * \\@version 0.0.2
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
