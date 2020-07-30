package net.vortexdata.netwatchdog.modules.component;

import java.util.ArrayList;

public abstract class BaseComponent {

    protected String address;
    protected String name;
    protected ArrayList<PerformanceClass> performanceClasses;

    public BaseComponent(String address, String name, ArrayList<PerformanceClass> performanceClasses) {
        this.address = address;
        this.name = name;
        this.performanceClasses = performanceClasses;
    }

    public abstract PerformanceClass runPerformanceCheck();

    protected PerformanceClass getPerformanceClassByResponseTime(int responseTime) {
        for (PerformanceClass pc : performanceClasses) {
            if (pc.getResponseTimeRange()[0] <= responseTime && pc.getResponseTimeRange()[1] >= responseTime)
                return pc;
        }
        return null;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public ArrayList<PerformanceClass> getPerformanceClasses() {
        return performanceClasses;
    }
}
