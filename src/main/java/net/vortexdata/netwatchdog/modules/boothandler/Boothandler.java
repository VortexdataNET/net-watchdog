package net.vortexdata.netwatchdog.modules.boothandler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Boothandler {

    public static LocalDateTime bootStart;
    public static LocalDateTime bootEnd;

    public static float getBootTimeMillis() {
        return ChronoUnit.NANOS.between(bootStart, bootEnd);
    }

}
