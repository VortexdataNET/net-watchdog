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

package net.vortexdata.netwatchdog.modules.component.components;

import net.vortexdata.netwatchdog.exceptions.InvalidComponentJSONException;
import net.vortexdata.netwatchdog.modules.component.performanceclasses.BasePerformanceClass;
import net.vortexdata.netwatchdog.modules.console.logging.Log;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * SocketComponent used to check socket endpoints or APIs.
 *
 * @author          Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class SocketComponent extends BaseComponent {

    private final int port;

    public SocketComponent(String filename, String address, ArrayList<BasePerformanceClass> basePerformanceClasses, boolean cachePerformanceClass, int port) {
        super(filename, address, basePerformanceClasses, cachePerformanceClass);
        this.port = port;
    }

    @Override
    protected BasePerformanceClass runPerformanceCheck() {
        try {
            long start = System.currentTimeMillis();
            Socket socket = new Socket(uri, port);
            socket.setSoTimeout(5000);
            //DataInputStream dis = new DataInputStream(socket.getInputStream());
            String response = "";
                    //dis.readUTF();
            long end = System.currentTimeMillis();
            socket.close();

            for (BasePerformanceClass pc : basePerformanceClasses)
                if (pc.lookupContent(response)) {
                    pc.setLastRecordedResponseTime((int) (end-start));
                    return pc;
                }


            // Fallback on response time base evaluation
            BasePerformanceClass pc = getPerformanceClassByResponseTime((int) (end-start));
            pc.setLastRecordedResponseTime((int) (end-start));
            return pc;
        } catch (IOException e) {
            return getPerformanceClassByResponseTime(-1);
        }
    }





    public static SocketComponent getSocketComponentFromJSON(JSONObject obj, String filename) throws InvalidComponentJSONException {

        String[] neededKeys = {
                "address"
        };
        if (!obj.keySet().containsAll(
                Arrays.asList(neededKeys)
        )) {
            throw new InvalidComponentJSONException("Can not construct SOCKET component from JSON as required keys are missing.");
        }

        String address = obj.getString("address");

        boolean cachePerformanceClass = true;
        try {
            if (obj.getString("cacheLastResult").equalsIgnoreCase("false"))
                cachePerformanceClass = false;
        } catch (Exception e) {
            Log.debug("Couldn't find cacheLastResult key, falling back to true.");
        }
        int port = 80;
        try {
            port = Integer.parseInt(obj.getString("port"));
        } catch (Exception e) {
            Log.error("Failed to parse port of component "+filename+", falling back to port 80.");
        }
        ArrayList<BasePerformanceClass> pcs = BasePerformanceClass.constructPerformanceClassesFromJSONArray(obj.getJSONArray("performanceClasses"));

        return new SocketComponent(
                filename,
                address,
                pcs,
                cachePerformanceClass,
                port
        );
    }

}