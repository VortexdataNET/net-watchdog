package net.vortexdata.netwatchdog.utils;

public enum Platform {
    WINDOWS("WINDOWS"),
    LINUX("LINUX"),
    MAC("MACOS");

    public static Platform getPlatformFromString(String platformRaw) {
        // Do regex magic
    }

    private final String regex;

    Platform(String regex) {
        this.regex = regex;
    }
}
