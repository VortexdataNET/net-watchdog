package net.vortexdata.netwatchdog.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utils class used to parse and transform dates.
 *
 * @author          Sandro Kierner
 * @since           0.0.0
 * @version         0.0.0
 */
public class DateUtils {

    public static String getPrettyStringFromLocalDateTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss y-M-d");
        return formatter.format(date);
    }

    public static LocalDateTime getLocalDateTimeFromStringISO(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime export = LocalDateTime.parse(date, formatter);
        return export;
    }

    public static String getISOStringFromLocalDateTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return formatter.format(date);
    }

    public static String getReadableDateString(LocalDateTime ldt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm");
        return formatter.format(ldt);
    }

}