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

package net.vortexdata.netwatchdog.modules.component;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.exceptions.InvalidComponentJSONException;
import net.vortexdata.netwatchdog.modules.component.types.RestComponent;
import net.vortexdata.netwatchdog.modules.component.types.SocketComponent;
import net.vortexdata.netwatchdog.modules.console.logging.Log;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

/**
 * Owner class of all loaded components. Manages and loads components.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class ComponentManager {

    public static final String COMPONENTS_DIR = "components//";

    private final ArrayList<File> unloadedComponents;
    private final ArrayList<BaseComponent> components;
    private final NetWatchdog netWatchdog;

    public ComponentManager(NetWatchdog netWatchdog) {
        components = new ArrayList<>();
        unloadedComponents = new ArrayList<>();
        this.netWatchdog = netWatchdog;
    }

    /**
     * Tries to load a and parse component from file.
     *
     * @param   filename    {@link String} containing path to file.
     * @return              {@link BaseComponent} object parsed from file;
     *                      <code>null</code> if file was not found or configuration
     *                      is invalid.
     */
    public BaseComponent loadComponent(String filename) throws InvalidComponentJSONException {
        JSONObject obj = loadComponentJSON(filename);
        if (obj == null)
            throw new InvalidComponentJSONException("Component JSON object must not be null.");

        // Check if component is already loaded
        if (getComponentByFilename(filename) != null) {
            Log.error("Component " + filename + " can not be loaded as it already is.");
            return null;
        }

        // Determine type of component
        if (obj.getString("type").equalsIgnoreCase("REST")) {
            try {
                Log.debug("Component " + filename + "is a REST component.");
                return RestComponent.getRestComponentFromJSON(obj, filename);
            } catch (InvalidComponentJSONException e) {
                Log.error("Component "+filename+"'s configuration is invalid and can not be loaded and enabled.", e);
            }
        } else if (obj.getString("type").equalsIgnoreCase("SOCKET")) {
            try {
                Log.debug("Component " + filename + "is a SOCKET component.");
                return SocketComponent.getSocketComponentFromJSON(obj, filename);
            } catch (InvalidComponentJSONException e) {
                Log.error("Component "+filename+"'s configuration is invalid and can not be loaded and enabled.", e);
            }
        } else {
            Log.error("Component " + filename + " uses unsupported component type " + obj.getString("type") + ". Please check configuration and try again.");
        }
        return null;
    }

    /**
     * Loads content from file as {@link JSONObject}.
     *
     * @param   filename    {@link String} containing path to file.
     * @return              {@link JSONObject} containing file content;
     *                      <code>null</code> if file was not found or an error occurred.
     */
    private JSONObject loadComponentJSON(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(COMPONENTS_DIR + filename));
            StringBuilder sb = new StringBuilder();
            while (br.ready())
                sb.append(br.readLine());
            try {
                return new JSONObject(sb.toString());
            } catch (Exception e) {
                Log.error("Component " + filename + " failed to load.", e);
            }
            br.close();
        } catch (FileNotFoundException e) {
            Log.error("Component " + filename + " file could not be found.", e);
        } catch (IOException e) {
            Log.error("Component " + filename + " could not be loaded due to an IO error.", e);
        }
        return null;
    }

    /**
     * Tries to load and parse all component files contained in components directory.
     *
     * @return              <code>true</code> if some components were loaded;
     *                      <code>false</code> if no components were loaded.
     */
    public boolean loadAll() {
        unloadedComponents.clear();
        components.clear();
        Log.info("Component files are being indexed and loaded...");
        File file = new File(COMPONENTS_DIR);
        file.mkdirs();

        FilenameFilter compFileFilter = new FilenameFilter(){
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".json");
            }
        };

        File[] fileList = file.listFiles(compFileFilter);
        if (fileList.length == 0) {
            Log.info("There are no components to load.");
            return false;
        }

        for (int i = 0; i < fileList.length; i++) {
            enableComponent(fileList[i].getName());
        }
        return true;
    }




    /**
     * Tries to load and enable component by filename.
     *
     * @param filename                  {@link String} containing path to component file.
     *
     * @return                          <code>true</code> if component was loaded successfully;
     *                                  <code>false</code> if component could not be loaded.
     */
    public boolean enableComponent(String filename) {
        Log.info("Component " + filename + " is being loaded and enabled...");
        BaseComponent c = null;
        try {
            c = loadComponent(filename);
            if (c == null)
                return false;

            unloadedComponents.removeIf(f -> f.getName().equals(filename));
            components.add(c);
            Log.info("Component " + c.getFilename() + " successfully enabled.");
            return true;
        } catch (InvalidComponentJSONException e) {
            Log.error("Component " + filename + " could not be enabled.", e);
            return false;
        }
    }

    /**
     * Disables component by filename.
     *
     * @param filename      {@link String} specifying the target component file name.
     * @return              <code>true</code> if component has been disabled;
     *                      <code>false</code> if component was not found.
     */
    public boolean disableComponent(String filename) {
        Log.debug("Component " + filename + " is being disabled...");
        BaseComponent c = getComponentByFilename(filename);
        if (c != null) {
            unloadedComponents.add(new File(ComponentManager.COMPONENTS_DIR + c.getFilename()));
            components.removeIf(x -> x.getFilename().equalsIgnoreCase(c.getFilename()));
            Log.debug("Component " + filename + " has been disabled.");
            return true;
        } else {
            Log.debug("Component " + filename + " could not be disabled as it was not found.");
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

    public ArrayList<File> getUnloadedComponents() {
        return unloadedComponents;
    }
}
