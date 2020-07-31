package net.vortexdata.netwatchdog.modules.component;

import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class BaseComponent {

    protected LocalDateTime lastCheck;
    protected String address;
    protected String name;
    protected ArrayList<PerformanceClass> performanceClasses;
    protected String filename;

    public BaseComponent(String address, String name, String filename, ArrayList<PerformanceClass> performanceClasses) {
        this.address = address;
        this.name = name;
        this.performanceClasses = performanceClasses;
        this.filename = filename;
    }

    public PerformanceClass check() {
        lastCheck = LocalDateTime.now();
        return runPerformanceCheck();
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

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public String getFilename() {
        return filename;
    }
}
