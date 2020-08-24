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

package net.vortexdata.netwatchdog.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.cli.CLI;

import java.util.HashMap;

/**
 * Base class for CLI commands.
 *
 * @author  Sandro Kierner
 * \@since 0.0.1
 * \\\@version 0.0.3
 */
public abstract class BaseCommand {

    protected String description;
    protected HashMap<String, String> args;
    protected String name;
    protected NetWatchdog netWatchdog;

    public BaseCommand(NetWatchdog netWatchdog, String name, String description) {
        this.netWatchdog = netWatchdog;
        this.name = name;
        args = new HashMap<>();
        this.description = description;
    }

    public String getHelpMessage() {
        StringBuilder sb = new StringBuilder();

        if (args == null || args.size() == 0)
            if (description != null && description.isEmpty())
                return description;
            else
                return "Help message unavailable.";

        sb.append(description + "\n\n");
        sb.append("Usage: ");
        sb.append(getName() + " ");

        sb.append(generateArgsString() + "\n\n");

        sb.append("More information about all arguments:\n\n");
        for (String key : args.keySet()) {
            sb.append("- " + key + ": " + args.get(key) + "\n");
        }

        return sb.toString();
    }

    private String generateArgsString() {
        if (args == null || args.size() == 0)
            return "N/A";

        String export = "<";
        String[] keys = args.keySet().toArray(new String[args.size()]);
        for (int i = 0; i < keys.length; i++) {
            if (keys.length - i == 1) {
                export += keys[i] + ">";
                break;
            } else if (i == 0) {
                export += keys[i] + " | ";
            } else {
                export += keys[i] + " | ";
            }
        }
        return export;
    }

    protected void printUsage() {
        CLI.print(getHelpMessage());
    }

    public abstract void call(String[] args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<String, String> getArgs() {
        return args;
    }
}
