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

package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.config.configs.NorthstarConfig;
import net.vortexdata.netwatchdog.modules.console.logging.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Class managing Northstar systems.
 *
 * @author Sandro Kierner
 * @version 0.2.0
 * @since 0.1.0
 */
public class NorthstarRegister {

    private ArrayList<NorthstarBase> northstars;
    private NetWatchdog netWatchdog;

    public NorthstarRegister(NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
        northstars = new ArrayList<>();
        NorthstarConfig northstarConfig = (NorthstarConfig) netWatchdog.getConfigRegister().getConfigByPath(NorthstarConfig.CONFIG_PATH);
        JSONArray array = northstarConfig.getValue().getJSONArray("northstars");
        for (int i = 0; i < array.length(); i++) {
            JSONObject ns = array.getJSONObject(i);
            NorthstarBase nb = NorthstarFactory.getNorthstarFromJSON(ns, this);
            if (nb != null)
                northstars.add(nb);
        }
    }

    public int getAvailabilityPercentage() {

        int threadCount = 1;
        try {
            threadCount = netWatchdog.getConfigRegister().getNorthstarConfig().getValue().getInt("threadCount");
        } catch (Exception e) {
            threadCount = 1;
        }

        double successful = 0;

        if (threadCount < 2) {
            Log.debug("Determining Northstar availability sequentially...");
            if (netWatchdog.getAppInfo().getPlatform() == null)
                return 0;
            for (NorthstarBase n : northstars) {
                if (n.isAvailable())
                    successful++;
            }
        } else {
            Log.debug("Determining Northstar availability multithreaded ("+threadCount+" threads)...");
            try {
                ArrayList<Future> availabilities = new ArrayList<Future>();
                ExecutorService tpe = Executors.newFixedThreadPool(threadCount);
                for (NorthstarBase n : northstars) {
                    availabilities.add(tpe.submit(n::isAvailable));
                }
                tpe.shutdown();
                tpe.awaitTermination(10, TimeUnit.SECONDS);
                for (Future f : availabilities) {
                    if ((boolean) f.get())
                        successful++;
                }
            } catch (InterruptedException | ExecutionException e) {
                Log.error("Failed to determine multithreaded northstar availability percentage, appending error message: " + e.getMessage());
            }
        }

        return (int) ((successful / (double) northstars.size()) * 100);

    }

    public ArrayList<NorthstarBase> getNorthstars() {
        return northstars;
    }

    public NetWatchdog getNetWatchdog() {
        return netWatchdog;
    }

    public NetWatchdog getNetwatchdog() {
        return netWatchdog;
    }

}
