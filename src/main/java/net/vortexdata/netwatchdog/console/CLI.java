package net.vortexdata.netwatchdog.console;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class CLI {

    public static LineReader lineReader;

    public static void init(CommandRegister register) {
        Terminal terminal = null;

        try {
            terminal = TerminalBuilder
                    .builder()
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArgumentCompleter ac = register.getCommandNameArgumentCompleter();
        ac.setStrict(true);

        lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(ac)
                .build();
    }

    public static void print(String msg) {
        lineReader.printAbove(msg);
    }

    public static String readLine(String prefix) {
        return lineReader.readLine(prefix);
    }

}
