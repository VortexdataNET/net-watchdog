package net.vortexdata.netwatchdog.modules.component;

import net.vortexdata.netwatchdog.NetWatchdog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ComponentManager {

    public static final String COMPONENTS_DIR = "components//";

    private ArrayList<BaseComponent> components;
    private NetWatchdog netWatchdog;

    public ComponentManager(NetWatchdog netWatchdog) {
        components = new ArrayList<>();
        this.netWatchdog = netWatchdog;
    }

    public BaseComponent loadComponent(String filename) {
        JSONObject obj = loadComponentJSON(filename);

        boolean hasFatalFlaw = false;
        try {
            if (!obj.has("name")) {
                netWatchdog.getLogger().error("Can not construct component of file " + filename + " as its name is not configured.");
                hasFatalFlaw = true;
            }
        } catch (Exception e) {
            netWatchdog.getLogger().error("Can not construct component of file " + filename + " as its name is not configured.");
            hasFatalFlaw = true;
        } try {
            if (!obj.has("address")) {
                netWatchdog.getLogger().error("Can not construct component of file " + filename + " as its address is not configured.");
                hasFatalFlaw = true;
            }
        } catch (Exception e) {
            netWatchdog.getLogger().error("Can not construct component of file " + filename + " as its address is not configured.");
            hasFatalFlaw = true;
        } try {
            if (!obj.has("performanceClasses")) {
                netWatchdog.getLogger().error("Can not construct component of file " + filename + " as its performance classes are not configured.");
                hasFatalFlaw = true;
            }
        } catch (Exception e) {
            netWatchdog.getLogger().error("Can not construct component of file " + filename + " as its performance classes are not configured.");
            hasFatalFlaw = true;
        }

        if (hasFatalFlaw)
            return null;



        if (obj.getString("type").equalsIgnoreCase("REST")) {
            return RestComponent.getRestComponentFromJSON(obj, netWatchdog);
        } else if (obj.getString("type").equalsIgnoreCase("SOCKET")) {

        } else if (obj.getString("type").equalsIgnoreCase("PING")) {

        } else {
            netWatchdog.getLogger().error("Unknown component type " + obj.getString("type") + ". Please check configuration and try again.");
        }
        return null;
    }

    private JSONObject loadComponentJSON(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(COMPONENTS_DIR + filename));
            StringBuilder sb = new StringBuilder();
            while (br.ready())
                sb.append(br.readLine());
            try {
                return new JSONObject(sb.toString());
            } catch (Exception e) {
                netWatchdog.getLogger().error("Failed to load component " + filename + ", appending error message: \n"+e.getMessage());
            }
        } catch (FileNotFoundException e) {
            netWatchdog.getLogger().error("Failed to load component " + filename + " as its file could not be found.");
        } catch (IOException e) {
            netWatchdog.getLogger().error("Failed to load component " + filename + ", appending error message: " + e.getMessage());
        }
        return null;
    }

    public boolean loadAll() {
        netWatchdog.getLogger().info("Trying to load and initiate all components...");
        File file = new File(COMPONENTS_DIR);
        file.mkdirs();
        FilenameFilter compFileFilter = new FilenameFilter(){
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith("-component.conf")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File[] fileList = file.listFiles(compFileFilter);
        if (fileList.length == 0) {
            netWatchdog.getLogger().info("There are no components to load.");
            return true;
        }
        for (int i = 0; i < fileList.length; i++) {
            netWatchdog.getLogger().debug("Trying to load component " + fileList[i].getName() + "...");
            BaseComponent c = loadComponent(fileList[i].getName());
            if (c != null) {
                if (getComponentByName(c.getName()) == null) {
                    netWatchdog.getLogger().error("Skipping addition of component " + c.getName() + " as this name is already used by another one.");
                } else {
                    netWatchdog.getLogger().debug("Adding component " + c.getName() + " to component registry.");
                    components.add(c);
                }
            }
        }
        return false;
    }

    public BaseComponent getComponentByName(String componentName) {
        for (BaseComponent c : components) {
            if (c.getName().equalsIgnoreCase(componentName))
                return c;
        }
        return null;
    }

    public static ArrayList<PerformanceClass> constructPerformanceClassesFromJSONArray(NetWatchdog netWatchdog, String componentName, JSONArray array) {
        ArrayList<PerformanceClass> export = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            PerformanceClass pc = constructPerformanceClassFromJSON(netWatchdog, componentName, array.getJSONObject(i));
            if (pc != null)
                export.add(pc);
        }
        return export;
    }

    public static PerformanceClass constructPerformanceClassFromJSON(NetWatchdog netWatchdog, String componentName, JSONObject obj) {
        if (!obj.has("name")) {
            netWatchdog.getLogger().error("Can not construct performance class for component " + componentName + " as its name is not configured.");
            return null;
        }
        if (!obj.has("responseTimeRange")) {
            netWatchdog.getLogger().error("Can not construct performance class "+obj.has("name")+" for component " + componentName + " as its response time range is not configured.");
            return null;
        }
        if (!obj.has("webhookPosts")) {
            netWatchdog.getLogger().error("Can not construct performance class "+obj.has("name")+" for component " + componentName + " as no webhooks are configured.");
            return null;
        }

        String name = obj.getString("name");
        String[] responseTimeRange = obj.getString("responseTimeRange").split("-");
        int[] responseTimes = new int[2];
        try {
            responseTimes[0] = Integer.parseInt(responseTimeRange[0]);
            responseTimes[1] = Integer.parseInt(responseTimeRange[1]);
        } catch (Exception e) {
            netWatchdog.getLogger().error("Failed to parse response time range of performance class "+name+" in component " + componentName + ".");
            return null;
        }
        String contentLookup = obj.getString("contentLookup");
        ArrayList<PerformanceClassWebhook> webhooks = getPerformanceClassWebhooksFromJSONArray(netWatchdog, componentName, name, obj.getJSONArray("webhookPosts"));

        return new PerformanceClass(name, responseTimes, contentLookup, webhooks);

    }

    public static ArrayList<PerformanceClassWebhook> getPerformanceClassWebhooksFromJSONArray(NetWatchdog netWatchdog, String componentName, String pcName, JSONArray array) {
        ArrayList<PerformanceClassWebhook> performanceClassWebhooks = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (!obj.has("address")) {
                netWatchdog.getLogger().error("Failed to add performance class webhook for performance class " + pcName + " in component "+ componentName + " as its address is not defined.");
                continue;
            }

            HashMap<String, String> headers = new HashMap<String, String>();
            JSONArray headerarray = obj.getJSONArray("headers");
            for (int j = 0; j < headerarray.length(); j++) {
                String[] pair = headerarray.getString(j).split(":");
                if (pair.length != 2) {
                    netWatchdog.getLogger().warn("Skipping addition of webhook header " + headerarray.getString(j) + " as its malformed. Please consult documentation.");
                    continue;
                }
                headers.put(pair[0], pair[1]);
            }

            performanceClassWebhooks.add(new PerformanceClassWebhook(
                    obj.getString("address"),
                    headers,
                    obj.getString("body")
            ));
        }
        return performanceClassWebhooks;
    }

}
