package net.vortexdata.netwatchdog.modules.component;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;

public class PerformanceClass {

    private String name;
    // 0 = start, 1 = end value
    private int[] responseTimeRange;
    private int lastRecordedResponseTime;
    private String contentLookup;
    private ArrayList<PerformanceClassWebhooks> webhooks;

    public PerformanceClass(String name, int[] responseTimeRange, String contentLookup, ArrayList<PerformanceClassWebhooks> webhooks) {
        this.name = name;
        this.responseTimeRange = responseTimeRange;
        this.contentLookup = contentLookup;
        this.webhooks = webhooks;
    }

    public boolean lookupContent(String string) {
        return string.contains(contentLookup);
    }

    public void runWebhooks() {
        throw new NotImplementedException();
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
