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

package net.vortexdata.netwatchdog.modules.console.logging;

/**
 * API for sending log output to file and console.
 *
 * @author  Sandro Kierner
 * @since 0.0.0
 * @version 0.0.0
 */
public class Log {

    enum LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public static ch.qos.logback.classic.Logger LOGGER;

    public static void debug(String message) {
        log(message, null, LogLevel.DEBUG);
    }

    public static void info(String message) {
        log(message, null, LogLevel.INFO);
    }

    public static void warn(String message) {
        log(message, null, LogLevel.WARN);
    }

    public static void error(String message) {
        log(message, null, LogLevel.ERROR);
    }

    public static void error(String message, Throwable throwable) {
        log(message, throwable, LogLevel.ERROR);
    }

    private static void log(String message, Throwable throwable, LogLevel logLevel) {

        if (LOGGER == null) {
            System.out.println(message);
            return;
        }

        switch (logLevel) {
            case DEBUG:
                LOGGER.debug(message, throwable);
                break;
            case INFO:
                LOGGER.info(message, throwable);
                break;
            case WARN:
                LOGGER.warn(message, throwable);
                break;
            case ERROR:
                LOGGER.error(message, throwable);
                break;
        }
    }

}
