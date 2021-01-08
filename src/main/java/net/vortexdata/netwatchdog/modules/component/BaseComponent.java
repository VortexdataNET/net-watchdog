/*
 * MIT License
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

package net.vortexdata.netwatchdog.modules.component;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Base class used by all sub-components.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public abstract class BaseComponent {

    public static String FILE_EXTENSION = ".json";
    protected LocalDateTime lastCheck;
    protected String address;
    protected ArrayList<PerformanceClass> performanceClasses;
    protected String filename;
    protected boolean cachePerformanceClass;
    protected boolean hasPerformanceClassChanged;
    protected PerformanceClass lastPerformanceClass;

    public BaseComponent(String filename, String address, ArrayList<PerformanceClass> performanceClasses, boolean cachePerformanceClass) {
        this.address = address;
        this.performanceClasses = performanceClasses;
        this.filename = filename;
        this.hasPerformanceClassChanged = true;
        this.cachePerformanceClass = cachePerformanceClass;
    }

    /**
     * Checks availability of the component and tries to
     * match it to a fitting performance class.
     *
     * Also updates {@link BaseComponent#hasPerformanceClassChanged} and
     * {@link BaseComponent#lastPerformanceClass} values.
     *
     * @return  Matching {@link PerformanceClass}
     */
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

    /**
     * Runs check code and maps it to matching {@link PerformanceClass}.
     *
     * @return  Mapped {@link PerformanceClass}
     */
    protected abstract PerformanceClass runPerformanceCheck();

    /**
     * Tries to match response time to available {@link PerformanceClass}.
     *
     * @param   responseTime
     * @return                  Mapped {@link PerformanceClass}
     */
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
