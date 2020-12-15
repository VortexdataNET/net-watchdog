package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.config.configs.NorthstarConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Class managing Northstar systems.
 *
 * @author Sandro Kierner
 * @version 0.3.0
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
            netWatchdog.getLogger().debug("Determining Northstar availability sequentially...");
            if (netWatchdog.getAppInfo().getPlatform() == null)
                return 0;
            for (NorthstarBase n : northstars) {
                if (n.isAvailable())
                    successful++;
            }
        } else {
            netWatchdog.getLogger().debug("Determining Northstar availability multithreaded ("+threadCount+" threads)...");
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
                netWatchdog.getLogger().error("Failed to determine multithreaded northstar availability percentage, appending error message: " + e.getMessage());
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
