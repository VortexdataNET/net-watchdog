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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utils class used to parse and transform dates.
 *
 * @author          Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class DateUtils {

    /**
     * Get a quick-formatted string from {@link LocalDateTime} object.
     * 
     * @param date  {@link LocalDateTime} object to be formatted.
     * @return      {@link String} in format <code>hh:mm:ss y-M-d</code>.
     */
    public static String getPrettyStringFromLocalDateTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss y-M-d");
        return formatter.format(date);
    }

    /**
     * Get an easily readable string from {@link LocalDateTime} object.
     *
     * @param ldt   {@link LocalDateTime} object to be formatted.
     * @return      {@link String} in format <code>MMMM d, yyyy HH:mm</code>.
     */
    public static String getReadableDateString(LocalDateTime ldt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm");
        return formatter.format(ldt);
    }

}