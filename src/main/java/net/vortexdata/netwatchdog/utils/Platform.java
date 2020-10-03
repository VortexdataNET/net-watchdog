package net.vortexdata.netwatchdog.utils;

public enum Platform {
    WINDOWS("WIN"),
    LINUX("LINUX"),
    MAC("MAC");

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
