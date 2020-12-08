/*
 * NET Watchdog
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

import net.vortexdata.netwatchdog.NetWatchdog;

import java.util.ArrayList;

/**
 * @author  Sandro Kierner
 * @version 0.2.0
 * @since 0.0.1
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
