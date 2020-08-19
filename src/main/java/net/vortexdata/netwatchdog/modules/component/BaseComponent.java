package net.vortexdata.netwatchdog.modules.component;

import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class BaseComponent {

    protected LocalDateTime lastCheck;
    protected String address;
    protected String name;
    protected ArrayList<PerformanceClass> performanceClasses;
    protected String filename;
    protected boolean cachePerformanceClass;
    protected boolean hasPerformanceClassChanged;
    protected PerformanceClass lastPerformanceClass;

    public BaseComponent(String address, String name, String filename, ArrayList<PerformanceClass> performanceClasses, boolean cachePerformanceClass) {
        this.address = address;
        this.name = name;
        this.performanceClasses = performanceClasses;
        this.filename = filename;
        this.hasPerformanceClassChanged = true;
        this.cachePerformanceClass = cachePerformanceClass;
    }

    public PerformanceClass check() {
        lastCheck = LocalDateTime.now();
        PerformanceClass pc = runPerformanceCheck();
        if (lastPerformanceClass == null)
            lastPerformanceClass = pc;
        else
            hasPerformanceClassChanged = !lastPerformanceClass.equals(pc);

        if (hasPerformanceClassChanged)
            lastPerformanceClass = pc;
        return pc;
    }

    public abstract PerformanceClass runPerformanceCheck();

    protected PerformanceClass getPerformanceClassByResponseTime(int responseTime) {
        for (PerformanceClass pc : performanceClasses) {
            if (pc.getResponseTimeRange()[0] <= responseTime && pc.getResponseTimeRange()[1] >= responseTime)
                return pc;
        }
        return new FallbackPerformanceClass(responseTime, "");
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

    public boolean isCachePerformanceClass() {
        return cachePerformanceClass;
    }

    public boolean isHasPerformanceClassChanged() {
        return hasPerformanceClassChanged;
    }

    public PerformanceClass getLastPerformanceClass() {
        return lastPerformanceClass;
    }
}
