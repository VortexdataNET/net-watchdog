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

package net.vortexdata.netwatchdog.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Wrapper class holding project information such as version tag, build date, etc.
 *
 * @author          Sandro Kierner
 * @since 0.1.0
 * @version 0.2.0
 */
public class AppInfo {

    private JSONObject values;
    private Platform platform;

    public AppInfo() {
        values = new JSONObject();
        platform = Platform.getPlatformFromString(System.getProperty("os.name"));
    }

    /**
     * Loads project information baked into projects.json file.
     * @return        <code>true</code> if information has been loaded successfully;
     *                <code>false</code> if an error occurred during load.
     */
    public boolean loadProjectConfig() {

        StringBuffer sb = new StringBuffer();
        BufferedReader headBr = null;
        try {
            InputStream headIs = getClass().getResourceAsStream("/project.json");
            headBr = new BufferedReader(new InputStreamReader(headIs));
            while (headBr.ready()) {
                sb.append(headBr.readLine());
            }
        } catch (Exception e) {
            return false;
        }

        values = new JSONObject(sb.toString());
        return true;
    }

    public JSONObject getValues() {
        return values;
    }

    public String getVersionName() {
        if (values.has("versionName"))
            return values.getString("versionName");
        else
            return "0.0.0";
    }

    public String getArch() {
        return System.getProperty("os.arch");
    }

    public Platform getPlatform() {
        return platform;
    }

}
