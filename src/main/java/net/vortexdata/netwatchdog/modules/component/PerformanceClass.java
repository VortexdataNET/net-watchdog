package net.vortexdata.netwatchdog.modules.component;

import java.util.ArrayList;

public class PerformanceClass {

    private String name;
    // 0 = start, 1 = end value
    private int[] responseTimeRange;
    private int lastRecordedResponseTime;
    private String contentLookup;
    private ArrayList<PerformanceClassWebhook> webhooks;

    public PerformanceClass(String name, int[] responseTimeRange, String contentLookup, ArrayList<PerformanceClassWebhook> webhooks) {
        this.name = name;
        this.responseTimeRange = responseTimeRange;
        this.contentLookup = contentLookup;
        this.webhooks = webhooks;
    }

    public boolean lookupContent(String string) {
        return string.contains(contentLookup);
    }

    public void runWebhooks() {

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
