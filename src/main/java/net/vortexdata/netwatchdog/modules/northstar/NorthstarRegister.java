package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.config.configs.NorthstarConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class managing Northstar systems.
 *
 * @author Sandro Kierner
 * @version 0.0.0
 * @since 0.0.0
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

        netWatchdog.getLogger().debug("Determining Northstar availability...");

        if (netWatchdog.getPlatform() == null)
            return 0;

        double successful = 0;
        for (NorthstarBase n : northstars) {
            if (n.isAvailable())
                successful++;
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
