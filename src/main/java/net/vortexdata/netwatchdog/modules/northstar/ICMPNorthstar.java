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

import net.vortexdata.netwatchdog.utils.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ICMP Northstar class.
 *
 * @author Sandro Kierner
 * @version 0.2.0
 * @since 0.1.0
 */
public class ICMPNorthstar extends NorthstarBase {

    public ICMPNorthstar(NorthstarRegister northstarRegister, String address, int timeout, int samples) {
        super(northstarRegister, address, timeout, samples);
    }

    @Override
    public boolean isAvailable() {

        if (timeout < 0)
            timeout = 5000;

        ArrayList<Integer> pingResults = new ArrayList<>();

        ProcessBuilder processBuilder = new ProcessBuilder();
        if (northstarRegister.getNetWatchdog().getAppInfo().getPlatform() == null) {
            wasLastAttemptSuccessful = false;
            return false;
        } else if (northstarRegister.getNetWatchdog().getAppInfo().getPlatform() == Platform.LINUX || northstarRegister.getNetWatchdog().getAppInfo().getPlatform() == Platform.MAC) {
            processBuilder.command(("ping -c 1 -t "+address+" " + samples).split(" "));
        } else if (northstarRegister.getNetWatchdog().getAppInfo().getPlatform() == Platform.WINDOWS) {
            processBuilder.command(("ping "+address+" -n "+ samples).split(" "));
        }

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern pattern = Pattern.compile("(?<=\\=)[0-9]*.[0-9]*(?= ?ms)");
            while ((line = reader.readLine()) != null) {
                if (line.toUpperCase().contains("REPLY FROM") || line.toUpperCase().contains("BYTES FROM")) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        pingResults.add((int) Double.parseDouble(matcher.group()));
                    } else {
                        pingResults.add(-1);
                    }
                } else if (line.toUpperCase().contains("TIMED OUT") ||
                        line.toUpperCase().contains("UNREACHABLE") ||
                        line.toUpperCase().contains("FAILURE") ||
                        line.toUpperCase().contains("FAILED") ||
                        line.toUpperCase().contains("INVALID") ||
                        line.toUpperCase().contains("ERROR")
                ) {
                    wasLastAttemptSuccessful = false;
                    return false;
                }
            }
        } catch (IOException e) {
            northstarRegister.getNetWatchdog().getLogger().debug(e.getMessage());
            wasLastAttemptSuccessful = false;
            return false;
        }

        for (Integer i : pingResults) {
            if (i == -1 || i > timeout) {
                wasLastAttemptSuccessful = false;
                return false;
            }
        }

        wasLastAttemptSuccessful = true;
        return true;
    }

}
