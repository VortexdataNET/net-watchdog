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

package net.vortexdata.netwatchdog.modules.config;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.config.configs.BaseConfig;
import net.vortexdata.netwatchdog.modules.config.configs.MainConfig;
import net.vortexdata.netwatchdog.modules.config.configs.NorthstarConfig;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Class used to load, store and evaluate all system configs in.
 *
 * @author  Sandro Kierner
 * @version 0.1.1
 * @since 0.0.1
 */
public class ConfigRegister {

    private ArrayList<BaseConfig> configs;
    private boolean ignoreCriticalConfig;
    private final NetWatchdog netWatchdog;
    private boolean didCriticalConfigFail;
    private boolean wereConfigsUpdated;

    public ConfigRegister(NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
        this.ignoreCriticalConfig = false;
        configs = new ArrayList<>();
        configs.add(new MainConfig());
        configs.add(new NorthstarConfig());
        didCriticalConfigFail = false;
        wereConfigsUpdated = false;
        loadAll();
    }

    public boolean loadAll() {

        boolean didNoErrorOccur = true;

        for (BaseConfig config : configs) {
            config.load();
            Stack<String> errors = config.checkIntegrity();
            if (errors != null && !errors.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String s : errors) {
                    if (s == null)
                        break;
                    sb.append(s).append("\n");
                }
                netWatchdog.getLogger().error("Integrity check for config " + config.getPath() + " failed, logging error stack: \n" + sb.toString());
                didNoErrorOccur = false;
            }
            if (!didNoErrorOccur && config.isCritical())
                didCriticalConfigFail = true;
            if (config.hasBeenUpdated())
                wereConfigsUpdated = true;
        }



        return didNoErrorOccur;
    }

    public NetWatchdog getNetWatchdog() {
        return netWatchdog;
    }

    public BaseConfig getConfigByPath(String path) {
        for (BaseConfig config : configs)
            if (config.getPath().equals(path))
                return config;
        return null;
    }

    public void setIgnoreCriticalConfig() {
        this.ignoreCriticalConfig = true;
    }

    public MainConfig getMainConfig() {
        return (MainConfig) getConfigByPath(MainConfig.CONFIG_PATH);
    }

    public boolean ignoringCriticalConfigFail() {
        return ignoreCriticalConfig;
    }

    public boolean didCriticalConfigFail() {
        return didCriticalConfigFail;
    }

    public boolean wereConfigsUpdated() {
        return wereConfigsUpdated;
    }

    public ArrayList<BaseConfig> getUpdatedConfigs() {
        ArrayList<BaseConfig> updatedConfigs = new ArrayList<>();
        for (BaseConfig c : configs)
            if (c.hasBeenUpdated())
                updatedConfigs.add(c);
        return updatedConfigs;
    }

}
