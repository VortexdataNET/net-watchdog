package net.vortexdata.netwatchdog.console;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;

public final class JLineAppender extends AppenderBase<ILoggingEvent> {

    private final Layout<ILoggingEvent> layout = new TTLLLayout();

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
