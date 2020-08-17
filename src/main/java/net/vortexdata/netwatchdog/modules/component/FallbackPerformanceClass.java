package net.vortexdata.netwatchdog.modules.component;

import net.vortexdata.netwatchdog.NetWatchdog;

import java.util.ArrayList;

/**
 * @author  Sandro Kierner
 * @version 0.0.0
 * @since   0.0.0
 *
 * This performance class is a placeholder / dummy class which is
 * returned if no performance class could be found meeting the
 * response time or other criteria in the check component method.
 *
 * It is primarily used for debugging purpose to get a response
 * time back to Query thead.
 */
public class FallbackPerformanceClass extends PerformanceClass {

    private final int responseTime;
    private final String info;

    public FallbackPerformanceClass(int responseTime, String additionalInformation) {
        super("Fallback", null, null, null, null);
        this.responseTime = responseTime;
        this.info = additionalInformation;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public String getInfo() {
        return info;
    }
}
