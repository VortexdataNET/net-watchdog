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

package net.vortexdata.netwatchdog.modules.console.cli;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.logging.Log;

/**
 * Input reader CLI component.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class ConsoleThread extends Thread {

    private final CommandRegister commandRegister;
    private boolean active;

    public ConsoleThread(CommandRegister commandRegister) {
        active = true;
        this.commandRegister = commandRegister;
    }

    @Override
    public void run() {
        active = true;
        while (active) {
            String input = "";
            try {
                input = CLI.readLine("> ");
                if (input.length() > 0 && !commandRegister.evaluateCommand(input))
                    if (input.split(" ").length > 0)
                        CLI.print(input.split(" ")[0] + ": Command not found");
            } catch (Exception e) {
                Log.error("An error occurred whilst trying to parse CLI input.", e);
            }
        }
    }

    public void end() {
        active = false;
    }

}
