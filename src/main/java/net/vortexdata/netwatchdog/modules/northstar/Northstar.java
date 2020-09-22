package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.utils.Platform;

import java.net.Socket;

public class Northstar {

    public Northstar(NorthstarRegister northstarRegister, String address) {
        this.northstarRegister = northstarRegister;
        this.address = address;
    }

    private NorthstarRegister northstarRegister;
    private String address;

    public boolean isAvailable() {

        Platform platform = Platform.getPlatformFromString(System.getProperty("os.name"));

        if (platform != null) {

        } else {
            netWatchdog.getLogger().warn("");
        }

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader inputStream = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            String s = "";
            // reading output stream of the command
            while ((s = inputStream.readLine()) != null) {
                System.out.println(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
