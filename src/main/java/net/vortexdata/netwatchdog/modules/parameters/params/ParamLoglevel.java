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

import ch.qos.logback.classic.Level;
import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.logging.Log;

/**
 * Overrides the default log level of the app.
 *
 * @author  Sandro Kierner
 * @since 0.1.0
 * @version 0.3.0
 */
public class ParamLoglevel extends ParamBase {

    public ParamLoglevel() {
        super("logLevel", "ll");
    }

    @Override
    public boolean runPreparation(String[] args, String calledName, NetWatchdog netWatchdog) {

        ch.qos.logback.classic.Logger logger = Log.LOGGER;

        if (args.length == 0) {
            Log.warn("Missing log level argument for start parameter "+calledName+", falling back to log level INFO.");
            logger.setLevel(Level.INFO);
            return false;
        } else {
            if (args[0].equalsIgnoreCase("DEBUG")) {
                Log.debug("Using log level DEBUG.");
                logger.setLevel(Level.DEBUG);
            } else if (args[0].equalsIgnoreCase("INFO")) {
                Log.debug("Using log level INFO.");
                logger.setLevel(Level.INFO);
            } else if (args[0].equalsIgnoreCase("WARN")) {
                Log.debug("Using log level WARN.");
                logger.setLevel(Level.WARN);
            } else if (args[0].equalsIgnoreCase("ERROR")) {
                Log.debug("Using log level ERROR.");
                logger.setLevel(Level.ERROR);
            } else {
                Log.debug("Unknown log level "+args[0]+", falling back to log level INFO.");
                logger.setLevel(Level.INFO);
            }
        }

        return true;
    }
}
