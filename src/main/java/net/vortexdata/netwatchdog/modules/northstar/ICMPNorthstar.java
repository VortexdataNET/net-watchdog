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
                    return false;
                }
            }
        } catch (IOException e) {
            northstarRegister.getNetWatchdog().getLogger().debug(e.getMessage());
            return false;
        }

        for (Integer i : pingResults) {
            if (i == -1 || i > timeout)
                return false;
        }

        return true;
    }

}
