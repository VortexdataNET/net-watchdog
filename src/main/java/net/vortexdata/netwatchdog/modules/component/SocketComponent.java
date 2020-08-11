package net.vortexdata.netwatchdog.modules.component;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.utils.RequestMethod;
import net.vortexdata.netwatchdog.utils.RestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class SocketComponent extends BaseComponent {

    private int port;

    public SocketComponent(String address, String name, String filename, ArrayList<PerformanceClass> performanceClasses, int port) {
        super(address, name, filename, performanceClasses);
        this.port = port;
    }

    @Override
    public PerformanceClass runPerformanceCheck() {
        try {
            long start = System.currentTimeMillis();
            Socket socket = new Socket(address, port);
            socket.setSoTimeout(5000);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String response = dis.readUTF();
            long end = System.currentTimeMillis();
            socket.close();

            for (PerformanceClass pc : performanceClasses)
                if (pc.lookupContent(response))
                    return pc;

            // Fallback on response time base evaluation
            return getPerformanceClassByResponseTime((int) (end-start));
        } catch (IOException e) {
            return getPerformanceClassByResponseTime(-1);
        }
    }

    public static SocketComponent getSocketComponentFromJSON(JSONObject obj, NetWatchdog netWatchdog) {
        String name = obj.getString("name");
        String address = obj.getString("address");
        String filename = obj.getString("filename");
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
                port
        );
    }

}
