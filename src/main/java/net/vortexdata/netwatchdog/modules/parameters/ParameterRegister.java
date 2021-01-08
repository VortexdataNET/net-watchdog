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

package net.vortexdata.netwatchdog.modules.parameters;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.logging.Log;
import net.vortexdata.netwatchdog.modules.parameters.params.ParamBase;
import net.vortexdata.netwatchdog.modules.parameters.params.ParamIgnoreCriticalConfig;
import net.vortexdata.netwatchdog.modules.parameters.params.ParamLoglevel;

import java.util.ArrayList;

/**
 * Register call that initializes and holds all
 * launch parameter classes. It also evaluates
 * and runs given launch parameters.
 *
 * @author  Sandro Kierner
 * @since 0.1.0
 * @version 0.3.0
 */
public class ParameterRegister {

    private String[] args;
    private ArrayList<ParamBase> params;
    private NetWatchdog netWatchdog;

    public ParameterRegister(String[] args, NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
        this.args = args;
        this.params = new ArrayList<>();

        params.add(new ParamLoglevel());
        params.add(new ParamIgnoreCriticalConfig());
    }

    /**
     * Evaluates arguments contained in args array
     * and calls parameter objects if applicable.
     */
    public void evaluateArguments() {

        if (args.length == 0) {
            Log.debug("No arguments found, skipping evaluation.");
            return;
        } else {
            Log.debug("Found " + args.length + " arguments.");
        }

        String calledName = "";
        ParamBase pb = null;
        ArrayList<String> pbArgs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                pbArgs.add(args[i]);
            } else {
                if (pb != null) {
                    pb.runPreparation(pbArgs.toArray(new String[pbArgs.size()]), calledName, netWatchdog);
                    calledName = "";
                    pb = null;
                    pbArgs.clear();
                }
                if (args[i].startsWith("--")) {
                    calledName = args[i].substring(2);
                    pb = getParamFromFull(calledName);
                } else if (args[i].startsWith("-")) {
                    calledName = args[i].substring(1);
                    pb = getParamFromShort(calledName);
                }
            }
            if (i == args.length-1 && pb != null)
                pb.runPreparation(pbArgs.toArray(new String[pbArgs.size()]), calledName, netWatchdog);
        }
    }

    public ParamBase getParam(String longOrShortName) {
        if (longOrShortName.startsWith("--")) {
            longOrShortName = longOrShortName.substring(2);
            return getParamFromFull(longOrShortName);
        } else if (longOrShortName.startsWith("-")) {
            longOrShortName = longOrShortName.substring(1);
            return getParamFromShort(longOrShortName);
        } else {
            if (getParamFromShort(longOrShortName) != null)
                return getParamFromShort(longOrShortName);
            else
                return getParamFromFull(longOrShortName);
        }
    }

    public ParamBase getParamFromShort(String shortName) {
        for (ParamBase pb : params)
            if (pb.getShortName().equalsIgnoreCase(shortName))
                return pb;
        return null;
    }

    public ParamBase getParamFromFull(String fullName) {
        for (ParamBase pb : params)
            if (pb.getFullName().equalsIgnoreCase(fullName))
                return pb;
        return null;
    }

    public String[] getArgs() {
        return args;
    }

    public ArrayList<ParamBase> getParams() {
        return params;
    }

    public NetWatchdog getNetWatchdog() {
        return netWatchdog;
    }
}
