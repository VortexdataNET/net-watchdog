package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.config.configs.NorthstarConfig;
import org.json.JSONArray;

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
        this.netWatchdog = new NetWatchdog();
        northstars = new ArrayList<>();
        NorthstarConfig northstarConfig = (NorthstarConfig) netWatchdog.getConfigRegister().getConfigByPath(NorthstarConfig.CONFIG_PATH);
        JSONArray array = northstarConfig.getValue().getJSONArray("addresses");
        for (int i = 0; i < array.length(); i++) {
            northstars.add(new NorthstarBase(this, array.getString(i)));
        }
    }

    public int getAvailabilityPercentage() throws PlatformNotSupportedException {
        if (netWatchdog.getPlatform() == null)
            throw new PlatformNotSupportedException("Your operating systems ping program is not supported by the app.");

        double successful = 0;
        for (NorthstarBase n : northstars) {
            if (n.isAvailable())
                successful++;
        }

        return (int) (successful / northstars.size()) * 100;
    }

    public ArrayList<NorthstarBase> getNorthstars() {
        return northstars;
    }

    public NetWatchdog getNetWatchdog() {
        return netWatchdog;
    }
}
