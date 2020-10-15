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

package net.vortexdata.netwatchdog.modules.console.cli;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

/**
 * CLI JLine utility class used to asynchronously print to and read
 * from command line.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.1.0
 */
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

    /**
     * Promps the user with a Yes/No dialogue.
     * @return true if the user entered YES or Y
     */
    public static boolean promptYesNo(String message) {
        String input = CLI.readLine(message + " [y/n]: ");
        return (input.equalsIgnoreCase("YES") || input.equalsIgnoreCase("Y"));
    }

}
