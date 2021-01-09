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

package net.vortexdata.netwatchdog.modules.console.cli.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import org.jline.utils.AttributedStringBuilder;

/**
 * Help command listing all available commands.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class HelpCommand extends BaseCommand {

    public HelpCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "help", "Get a list of all commands.");
        this.args.put("command", "Creates a new target.");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            BaseCommand c = netWatchdog.getCommandRegister().getCommandByName(args[0]);
            if (c != null) {
                c.printUsage();
            } else {
                CLI.print("Unknown command.");
            }
        } else {
            AttributedStringBuilder builder = new AttributedStringBuilder();
            builder.append("The following commands are supported at the moment:\n\n");
            StringBuilder sb = new StringBuilder();
            for (BaseCommand c : netWatchdog.getCommandRegister().getCommands()) {
                sb.append(String.format("%-32s%-32s", c.getName(), c.getDescription())).append("\n");
            }

            CLI.print(builder.toAnsi());
            CLI.print(sb.toString());
        }
    }

}
