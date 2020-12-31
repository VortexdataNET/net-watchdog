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

import org.json.JSONObject;

/**
 * Logic wrapper used to construct Northstar instances.
 *
 * @author Sandro Kierner
 * @version 0.2.0
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
