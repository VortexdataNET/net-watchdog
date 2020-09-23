package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.utils.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (northstarRegister.getNetWatchdog().getPlatform() == null) {
            return false;
        } else if (northstarRegister.getNetWatchdog().getPlatform() == Platform.LINUX || northstarRegister.getNetWatchdog().getPlatform() == Platform.MAC) {
            processBuilder.command(("ping -t "+address+" " + samples).split(" "));
        } else if (northstarRegister.getNetWatchdog().getPlatform() == Platform.WINDOWS) {
            northstarRegister.getNetWatchdog().getLogger().debug("Sending "+ ("ping "+address+" -n "+ samples));
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
