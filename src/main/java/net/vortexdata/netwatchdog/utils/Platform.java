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

/**
 * Enum class used to determine the
 * operating system platform the
 * app is running on.
 *
 * @author Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public enum Platform {
    WINDOWS("WIN"),
    LINUX("LINUX"),
    MAC("MAC");

    /**
     * Get a Platform enum by passing the OS name (e.g. by using System.getProperty("os.name"))
     * as <a href="#{@link}">{@link String}</a>string.
     *
     * @param   platformRaw     Platform name which is matched with platform regex.
     * @return                  The matching platform enum, null if no match is found.
     */
    public static Platform getPlatformFromString(String platformRaw) {
        platformRaw = platformRaw.toUpperCase();
        if (platformRaw.contains(WINDOWS.regex)) {
            return Platform.WINDOWS;
        } else if (platformRaw.contains(LINUX.regex)) {
            return Platform.LINUX;
        } else if (platformRaw.contains(MAC.regex)) {
            return Platform.MAC;
        } else {
            return null;
        }
    }

    private final String regex;

    Platform(String regex) {
        this.regex = regex;
    }
}
