package net.vortexdata.netwatchdog.modules.northstar;

import org.json.JSONObject;

/**
 * Logic wrapper used to construct Northstar instances.
 *
 * @author Sandro Kierner
 * @version 0.3.0
 * @since 0.1.0
 */
public class NorthstarFactory {

    public static NorthstarBase getNorthstarFromJSON(JSONObject jsonObject, NorthstarRegister nr) {

        String address = "";
        int port = -1;
        int samples = 0;
        int timeout = -1;

        if (jsonObject.has("address")) {
            address = jsonObject.getString("address");
        } else {
            nr.getNetWatchdog().getLogger().error("Can not construct Northstar as no address is defined.");
            return null;
        }

        String type = jsonObject.getString("type");
        if (!type.equalsIgnoreCase("ICMP") && !type.equalsIgnoreCase("SOCKET")) {
            nr.getNetWatchdog().getLogger().error("Can not construct Northstar with address "+address+". Type must either be SOCKET or ICMP.");
            return null;
        }


        if (type.equalsIgnoreCase("SOCKET")) {
            if (jsonObject.has("port"))
                port = jsonObject.getInt("port");
            else {
                nr.getNetWatchdog().getLogger().error("Can not construct Northstar with address "+address+". Port is missing.");
                return null;
            }


            if (jsonObject.has("timeout"))
                timeout = jsonObject.getInt("timeout");
            else
                timeout = 5000;
        }

        if (type.equalsIgnoreCase("ICMP")) {
            if (jsonObject.has("samples"))
                samples = jsonObject.getInt("samples");
            else
                samples = 1;
        }

        if (type.equalsIgnoreCase("ICMP")) {
            return new ICMPNorthstar(
                nr,
                address,
                timeout,
                samples
            );
        } else if (type.equalsIgnoreCase("SOCKET")) {
            return new SocketNorthstar(
                nr,
                address,
                timeout,
                port
            );
        }
        return null;
    }

}
