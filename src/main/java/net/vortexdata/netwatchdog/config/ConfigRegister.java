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

package net.vortexdata.netwatchdog.config;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.config.configs.MainConfig;

import java.util.Stack;

/**
 * Class used to load, store and evaluate all system configs in.
 *
 * @author  Sandro Kierner
 * @version 0.0.1
 * @since 0.0.1
 */
public class ConfigRegister {

    private final NetWatchdog netWatchdog;
    private final MainConfig mainConfig;

    public ConfigRegister(NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
        mainConfig = new MainConfig();
        loadAll();
    }

    public boolean loadAll() {
        loadMainConfig();
        return true;
    }

    public boolean loadMainConfig() {
        mainConfig.load();
        Stack<String> errors = mainConfig.checkIntegrity();
        if (mainConfig.checkIntegrity() != null && !mainConfig.checkIntegrity().isEmpty()) {
            netWatchdog.getLogger().error("Integrity check for main config failed, logging error stack:");
            for (String s : errors) {
                netWatchdog.getLogger().error(s);
            }
            netWatchdog.getLogger().info("Check check your main configuration for the above mentions error(s) and try again.");
            return false;
        }
        return true;
    }

    public NetWatchdog getNetWatchdog() {
        return netWatchdog;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }
}
