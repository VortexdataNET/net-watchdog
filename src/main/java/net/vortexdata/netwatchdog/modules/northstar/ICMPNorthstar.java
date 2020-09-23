package net.vortexdata.netwatchdog.modules.northstar;

import net.vortexdata.netwatchdog.utils.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ICMPNorthstar extends NorthstarBase {

    public ICMPNorthstar(NorthstarRegister northstarRegister, String address, int samples, int timeout) {
        super(northstarRegister, address, samples, timeout);
    }

    @Override
    public boolean isAvailable() {

        ArrayList<Integer> pingResults = new ArrayList<>();

        ProcessBuilder processBuilder = new ProcessBuilder();
        if (northstarRegister.getNetWatchdog().getPlatform() == null) {
            return false;
        } else if (northstarRegister.getNetWatchdog().getPlatform() == Platform.LINUX || northstarRegister.getNetWatchdog().getPlatform() == Platform.MAC) {
            processBuilder.command(("cmd.exe ping 1.1.1.1 -n "+ samples).split(" "));
        } else if (northstarRegister.getNetWatchdog().getPlatform() == Platform.WINDOWS) {
            processBuilder.command(("sh ping -t " + samples).split(" "));
        }

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern pattern = Pattern.compile("(?<=\\=)[0-9]*.[0-9]*(?= ?ms)");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find())
                    pingResults.add((int) Double.parseDouble(matcher.group()));
                else
                    pingResults.add(-1);
            }
        } catch (IOException e) {
            return false;
        }

        for (Integer i : pingResults) {
            if (i == -1 || i > timeout)
                return false;
        }

        return true;
    }

}
