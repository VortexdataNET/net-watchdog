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

package net.vortexdata.netwatchdog.modules.component.components;

import net.vortexdata.netwatchdog.modules.component.performanceclasses.BasePerformanceClass;
import net.vortexdata.netwatchdog.modules.component.performanceclasses.FallbackPerformanceClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Base class used by all sub-components.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public abstract class BaseComponent {

    public static final String FILE_EXTENSION = ".json";

    protected LocalDateTime lastCheck;
    protected final String uri;
    protected final ArrayList<BasePerformanceClass> basePerformanceClasses;
    protected final String filename;
    protected final boolean cachePerformanceClass;
    protected boolean hasPerformanceClassChanged;
    protected BasePerformanceClass lastBasePerformanceClass;
    protected LinkedList<String> criteria;

    public BaseComponent(String filename, String uri, ArrayList<BasePerformanceClass> basePerformanceClasses, boolean cachePerformanceClass, LinkedList<String> criteria) {
        this.uri = uri;
        this.basePerformanceClasses = basePerformanceClasses;
        this.filename = filename;
        this.hasPerformanceClassChanged = true;
        this.cachePerformanceClass = cachePerformanceClass;

        this.criteria = criteria;
    }

    /**
     * Checks availability of the component and tries to
     * match it to a fitting performance class.
     *
     * Also updates {@link BaseComponent#hasPerformanceClassChanged} and
     * {@link BaseComponent#lastBasePerformanceClass} values.
     *
     * @return  Matching {@link BasePerformanceClass}
     */
    public BasePerformanceClass check() {
        lastCheck = LocalDateTime.now();
        BasePerformanceClass pc = runPerformanceCheck();
        if (lastBasePerformanceClass == null)
            lastBasePerformanceClass = pc;
        else
            hasPerformanceClassChanged = !lastBasePerformanceClass.equals(pc);

        if (hasPerformanceClassChanged)
            lastBasePerformanceClass = pc;
        return pc;
    }

    /**
     * Runs check code and maps it to matching {@link BasePerformanceClass}.
     *
     * @return  Mapped {@link BasePerformanceClass}
     */
    protected abstract BasePerformanceClass runPerformanceCheck();

    /**
     * Tries to match response time to available {@link BasePerformanceClass}.
     *
     * @param   responseTime    Response time to find performance class for.
     * @return                  Mapped {@link BasePerformanceClass}
     */
    protected BasePerformanceClass getPerformanceClassByResponseTime(int responseTime) {
        for (BasePerformanceClass pc : basePerformanceClasses) {
            if (pc.getResponseTimeRange()[0] <= responseTime && pc.getResponseTimeRange()[1] >= responseTime)
                return pc;
        }
        return new FallbackPerformanceClass(responseTime, "");
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

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public LinkedList<String> getCriteria() {
        return criteriaQueue;
    }
}
