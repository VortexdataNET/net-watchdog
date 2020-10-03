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

package net.vortexdata.netwatchdog.utils;

import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * REST utils class used in REST components and performance class webhooks.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.0.1
 */
public class RestUtils {

    public static byte[] getPostBytes(String body) throws JSONException {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    public static HttpsURLConnection getPostConnection(byte[] data, String url, String contentType, HashMap<String, String> extraHeaders) throws IOException {
        HttpsURLConnection connection =
                (HttpsURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setConnectTimeout(5000);

        if (extraHeaders != null) {
            for (String key : extraHeaders.keySet()) {
                connection.setRequestProperty(key, extraHeaders.get(key));
            }
        }

        connection.setUseCaches(false);
        connection.setFixedLengthStreamingMode(data.length);
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();
        return connection;
    }

    public static HttpsURLConnection getGetConnection(String url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    public static HttpURLConnection getDeleteConnection(String url, String contentType) throws IOException {
        HttpURLConnection connection =
                (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", contentType);
        connection.getOutputStream().flush();
        return connection;
    }

    public static HttpURLConnection getDeleteConnection(byte[] data, String url, String contentType) throws IOException {
        HttpURLConnection connection =
                (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setFixedLengthStreamingMode(data.length);
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();
        return connection;
    }

    public static HttpURLConnection getPutConnection(byte[] data, String url, String contentType) throws IOException {
        HttpURLConnection connection =
                (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setFixedLengthStreamingMode(data.length);
        connection.getOutputStream().write(data);
        connection.getOutputStream().flush();
        return connection;
    }

    public static String readResponseStream(BufferedReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }


}
