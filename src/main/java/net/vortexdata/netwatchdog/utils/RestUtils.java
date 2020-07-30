package net.vortexdata.netwatchdog.utils;

import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

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
