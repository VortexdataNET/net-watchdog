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

package net.vortexdata.netwatchdog.modules.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;

import java.util.HashMap;

/**
 * Base class for CLI commands.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
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

    /**
     * Returns help message of command used in help
     * command.
     * @return      {@link String} representing help
     *              message.
     */
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

    /**
     * Generates args {@link String} from arguments {@link HashMap}.
     * Used in help message.
     *
     * @return  {@link String} formatted as args string.
     */
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

    /**
     * Prints help message to CLI.
     */
    protected void printUsage() {
        CLI.print(getHelpMessage());
    }

    /**
     * Runs command logic.
     * @param args      Array containing all user arguments
     *                  separated by whitespace.
     */
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
