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

public class SocketComponent extends BaseComponent {

    private final int port;

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
