package net.vortexdata.netwatchdog.modules.component;

import java.util.HashMap;

public class PerformanceClassWebhook {

    private String address;
    private HashMap<String, String> headers;
    private String body;

    public PerformanceClassWebhook(String address, HashMap<String, String> headers, String body) {
        this.address = address;
        this.headers = headers;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}
