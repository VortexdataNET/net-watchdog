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

package net.vortexdata.netwatchdog.modules.parameters.params;

import net.vortexdata.netwatchdog.NetWatchdog;

/**
 * Base class for all launch parameter classes.
 *
 * @author  Sandro Kierner
 * @since 0.1.0
 * @version 0.3.0
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class ParamBase {

    protected final String fullName;
    protected final String shortName;

    public ParamBase(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    /**
     * Runs parameter logic.
     *
     * @param args          String array containing arguments of the parameter call.
     * @param calledName    String defining by which name the parameter was called by user.
     * @param netWatchdog   {@link NetWatchdog} instance used to control the app.
     * @return              <code>true</code> if preparation finished successfully.
     *                      <code>false</code> if preparation failed.
     */
    public abstract boolean runPreparation(String[] args, String calledName, NetWatchdog netWatchdog);

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }
}
