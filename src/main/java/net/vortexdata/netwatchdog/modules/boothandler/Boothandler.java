package net.vortexdata.netwatchdog.modules.boothandler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class Boothandler {

    public static LocalDateTime bootStart;
    public static LocalDateTime bootEnd;
    public static LocalDateTime shutdown;

    public static float getBootTimeMillis() {
        return ChronoUnit.MILLIS.between(bootStart, bootEnd);
    }

    public static LocalDateTime getBootStart() {
        return bootStart;
    }

    public static LocalDateTime getBootEnd() {
        return bootEnd;
    }
}
