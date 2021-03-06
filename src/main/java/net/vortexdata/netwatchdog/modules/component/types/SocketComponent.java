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

package net.vortexdata.netwatchdog.modules.component.types;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.modules.component.PerformanceClass;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * SocketComponent used to check socket endpoints or APIs.
 *
 * @author          Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class SocketComponent extends BaseComponent {

    private final int port;

    public SocketComponent(String address, String name, String filename, ArrayList<PerformanceClass> performanceClasses, boolean cachePerformanceClass, int port) {
        super(address, name, filename, performanceClasses, cachePerformanceClass);
        this.port = port;
    }

    @Override
    protected PerformanceClass runPerformanceCheck() {
        try {
            long start = System.currentTimeMillis();
            Socket socket = new Socket(address, port);
            socket.setSoTimeout(5000);
            //DataInputStream dis = new DataInputStream(socket.getInputStream());
            String response = "";
                    //dis.readUTF();
            long end = System.currentTimeMillis();
            socket.close();

            for (PerformanceClass pc : performanceClasses)
                if (pc.lookupContent(response)) {
                    pc.setLastRecordedResponseTime((int) (end-start));
                    return pc;
                }


            // Fallback on response time base evaluation
            PerformanceClass pc = getPerformanceClassByResponseTime((int) (end-start));
            pc.setLastRecordedResponseTime((int) (end-start));
            return pc;
        } catch (IOException e) {
            return getPerformanceClassByResponseTime(-1);
        }
    }

    public static SocketComponent getSocketComponentFromJSON(JSONObject obj, NetWatchdog netWatchdog) {
        String name = obj.getString("name");
        String address = obj.getString("address");
        String filename = obj.getString("filename");
        boolean cachePerformanceClass = true;
        try {
            if (obj.getString("cacheLastResult").equalsIgnoreCase("false"))
                cachePerformanceClass = false;
        } catch (Exception e) {
            netWatchdog.getLogger().debug("Couldn't find cacheLastResult key, falling back to true.");
        }
        int port = 80;
        try {
            port = Integer.parseInt(obj.getString("port"));
        } catch (Exception e) {
            netWatchdog.getLogger().error("Failed to parse port of component " + name + ", falling back to port 80.");
        }
        ArrayList<PerformanceClass> pcs = ComponentManager.constructPerformanceClassesFromJSONArray(netWatchdog, name, obj.getJSONArray("performanceClasses"));

        return new SocketComponent(
                address,
                name,
                filename,
                pcs,
                cachePerformanceClass,
                port
        );
    }

}
