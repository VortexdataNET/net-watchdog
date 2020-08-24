/*
 * NET Watchdog
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

package net.vortexdata.netwatchdog.console.cli;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.util.CachingDateFormatter;

/**
 * Logging interface which uses JLine for async. CLI write / read.
 *
 * @author  Sandro Kierner
 * \@since 0.0.1
 * \@version 0.0.1
 */
public final class JLineAppender extends AppenderBase<ILoggingEvent> {

    class CustomLayout extends LayoutBase<ILoggingEvent> {

        CachingDateFormatter cachingDateFormatter = new CachingDateFormatter("HH:mm:ss.SSS");
        ThrowableProxyConverter tpc = new ThrowableProxyConverter();

        public void start() {
            this.tpc.start();
            super.start();
        }

        public String doLayout(ILoggingEvent event) {
            if (!this.isStarted()) {
                return "";
            } else {
                StringBuilder sb = new StringBuilder();
                long timestamp = event.getTimeStamp();
                sb.append("[");
                sb.append(this.cachingDateFormatter.format(timestamp));
                sb.append("]");
                sb.append(" [");
                sb.append(event.getLevel().toString());
                sb.append("]");
                sb.append("\t ");
                sb.append(event.getFormattedMessage());
                sb.append(CoreConstants.LINE_SEPARATOR);
                IThrowableProxy tp = event.getThrowableProxy();
                if (tp != null) {
                    String stackTrace = this.tpc.convert(event);
                    sb.append(stackTrace);
                }

                return sb.toString();
            }
        }

    }

    private final Layout<ILoggingEvent> layout = new CustomLayout();

    @Override
    public void start() {
        super.start();
        layout.start();
    }

    @Override
    public void stop() {
        layout.stop();
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        CLI.lineReader.printAbove(layout.doLayout(event));
    }


}
