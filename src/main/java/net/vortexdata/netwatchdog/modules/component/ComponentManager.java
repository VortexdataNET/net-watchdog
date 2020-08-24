/*
 * NET Watchdog
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

package net.vortexdata.netwatchdog.modules.component;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.component.types.RestComponent;
import net.vortexdata.netwatchdog.modules.component.types.SocketComponent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Owner class of all loaded components. Manages and loads components.
 *
 * @author  Sandro Kierner
 * \@since 0.0.1
 * \\@version 0.0.2
 */
public class ComponentManager {

    public static final String COMPONENTS_DIR = "components//";
    public static final String COMPONENT_IDENTIFIER = "-component.conf";

    private final ArrayList<File> unloadedComponents;
    private final ArrayList<BaseComponent> components;
    private final NetWatchdog netWatchdog;

    public ComponentManager(NetWatchdog netWatchdog) {
        components = new ArrayList<>();
        unloadedComponents = new ArrayList<>();
        this.netWatchdog = netWatchdog;
    }

    public BaseComponent loadComponent(String filename) {
        JSONObject obj = loadComponentJSON(filename);
        if (obj == null)
            return null;

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
        } try {
            String jsonFilename = obj.getString("filename");
            if (!(jsonFilename+ComponentManager.COMPONENT_IDENTIFIER).equals(filename)) {
                netWatchdog.getLogger().error("Component of file " + filename + " has non-matching filename parameter (expected: "+filename+", got: "+jsonFilename+ComponentManager.COMPONENT_IDENTIFIER+").");
                hasFatalFlaw = true;
            }
        } catch (Exception e) {
            netWatchdog.getLogger().error("Component of file " + filename + " is missing filename parameter. Please check configuration.");
            hasFatalFlaw = true;
        }

        if (hasFatalFlaw)
            return null;

        if (getComponentByName(obj.getString("name")) != null) {
            netWatchdog.getLogger().error("Can not load component " + obj.getString("name") + " as its either already loaded or another one with same name is loaded.");
            return null;
        }

        if (obj.getString("type").equalsIgnoreCase("REST")) {
            netWatchdog.getLogger().debug("Loading REST component...");
            return RestComponent.getRestComponentFromJSON(obj, netWatchdog);
        } else if (obj.getString("type").equalsIgnoreCase("SOCKET")) {
            netWatchdog.getLogger().debug("Loading SOCKET component...");
            return SocketComponent.getSocketComponentFromJSON(obj, netWatchdog);
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
            br.close();
        } catch (FileNotFoundException e) {
            netWatchdog.getLogger().error("Failed to load component " + filename + " as its file could not be found: " + e.getMessage());
        } catch (IOException e) {
            netWatchdog.getLogger().error("Failed to load component " + filename + ", appending error message: " + e.getMessage());
        }
        return null;
    }

    public boolean loadAll() {
        unloadedComponents.clear();
        components.clear();
        netWatchdog.getLogger().info("Trying to load and initiate all components...");
        File file = new File(COMPONENTS_DIR);
        file.mkdirs();
        FilenameFilter compFileFilter = new FilenameFilter(){
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith("-component.conf");
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
                if (getComponentByName(c.getName()) != null) {
                    netWatchdog.getLogger().error("Skipping addition of component " + c.getName() + " as this name is already used by another one.");
                } else {
                    netWatchdog.getLogger().info("Adding component " + c.getName() + " to component registry.");
                    components.add(c);
                }
            } else {
                unloadedComponents.add(fileList[i]);
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
            netWatchdog.getLogger().error("Can not construct performance class "+obj.get("name")+" for component " + componentName + " as its response time range is not configured.");
            return null;
        }
        if (!obj.has("webhookPosts")) {
            netWatchdog.getLogger().error("Can not construct performance class "+obj.get("name")+" for component " + componentName + " as no webhooks are configured.");
            return null;
        }

        String name = obj.getString("name");
        String[] responseTimeRange = obj.getString("responseTimeRange").split("-");
        int[] responseTimes = new int[2];
        try {
            responseTimes[0] = Integer.parseInt(responseTimeRange[0]);
            responseTimes[1] = Integer.parseInt(responseTimeRange[1]);
        } catch (Exception e) {
            if (responseTimeRange[0].equalsIgnoreCase("timeout")) {
                responseTimes[0] = -1;
                responseTimes[1] = -1;
            } else {
                netWatchdog.getLogger().error("Failed to parse response time range of performance class "+name+" in component " + componentName + ".");
                return null;
            }
        }
        String contentLookup = null;
        try {
            contentLookup = obj.getString("contentLookup");
        } catch (Exception e) {
            netWatchdog.getLogger().debug("Content lookup header not found for component " + componentName + ".");
        }

        ArrayList<PerformanceClassWebhook> webhooks = getPerformanceClassWebhooksFromJSONArray(netWatchdog, componentName, name, obj.getJSONArray("webhookPosts"));

        return new PerformanceClass(name, responseTimes, contentLookup, webhooks, netWatchdog);

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

            String body = null;
            try {
                body = obj.getString("body");
            } catch (Exception e) {
                netWatchdog.getLogger().debug("No body parameter found for performance class " + pcName + " in component " + componentName + ".");
            }

            performanceClassWebhooks.add(new PerformanceClassWebhook(
                    obj.getString("address"),
                    headers,
                    body
            ));
        }
        return performanceClassWebhooks;
    }

    public boolean enableComponent(String name, boolean appendCompIdentifier) {
        if (appendCompIdentifier)
            return enableComponent(name + ComponentManager.COMPONENT_IDENTIFIER);
        else
            return enableComponent(name);
    }

    public boolean enableComponent(String filename) {
        netWatchdog.getLogger().info("Trying to enable component " + filename + "...");
        BaseComponent c = loadComponent(filename);
        if (c == null) {
            netWatchdog.getLogger().error("Failed to enable component " + filename + ".");
            return false;
        } else {
            unloadedComponents.removeIf(f -> f.getName().equals(filename));
            components.add(c);
            netWatchdog.getLogger().info("Component " + c.getName() + " successfully enabled.");
            return true;
        }
    }

    public boolean disableComponent(String filename) {
        netWatchdog.getLogger().debug("Trying to disable component " + filename + "...");
        BaseComponent c = getComponentByFilename(filename);
        if (c != null) {
            netWatchdog.getLogger().debug("Disabling component "+filename+"...");
            unloadedComponents.add(new File(ComponentManager.COMPONENTS_DIR + c.getFilename()));
            components.removeIf(x -> x.getName().equalsIgnoreCase(c.getName()));
            return true;
        } else {
            netWatchdog.getLogger().debug("Component "+filename+" not found.");
            return false;
        }
    }

    public BaseComponent getComponentByFilename(String filename) {
        for (BaseComponent c : components) {
            if (c.getFilename().equals(filename))
                return c;
        }
        return null;
    }

    public ArrayList<BaseComponent> getComponents() {
        return components;
    }

    public NetWatchdog getNetWatchdog() {
        return netWatchdog;
    }

    public boolean unloadComponent(BaseComponent component) {
        return components.remove(component);
    }

    public ArrayList<File> getUnloadedComponents() {
        return unloadedComponents;
    }
}
