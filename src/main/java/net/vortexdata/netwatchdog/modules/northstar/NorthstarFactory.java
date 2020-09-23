package net.vortexdata.netwatchdog.modules.northstar;

import org.json.JSONObject;

public class NorthstarFactory {

    public static NorthstarBase getNorthstarFromJSON(JSONObject jsonObject, NorthstarRegister nr) {

        String address = "";
        int port = -1;
        int samples = 0;
        int timeout = -1;

        String type = jsonObject.getString("type");
        if (!type.equalsIgnoreCase("ICMP") && !type.equalsIgnoreCase("SOCKET")) {
            return null;
        }

        if (type.equalsIgnoreCase("SOCKET")) {
            port = jsonObject.getInt("port");
            timeout = jsonObject.getInt("timeout");
        }

        if (type.equalsIgnoreCase("ICMP")) {
            samples = jsonObject.getInt("samples");
        }

        if (type.equalsIgnoreCase("ICMP")) {
            return new ICMPNorthstar(
                nr,
                address,
                samples,
                timeout
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
