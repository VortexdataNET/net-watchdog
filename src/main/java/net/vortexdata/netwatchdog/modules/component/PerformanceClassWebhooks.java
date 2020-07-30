package net.vortexdata.netwatchdog.modules.component;

import java.util.HashMap;

public class PerformanceClassWebhooks {

    private String address;
    private HashMap<String, String> headers;
    private String body;

    public PerformanceClassWebhooks(String address, HashMap<String, String> headers, String body) {
        this.address = address;
        this.headers = headers;
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}
