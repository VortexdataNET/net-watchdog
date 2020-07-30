package net.vortexdata.netwatchdog.console;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.util.CachingDateFormatter;

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
