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
import net.vortexdata.netwatchdog.modules.console.commands.*;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Register of all available commands. Command classes must be instanced and added to
 * List to be usable.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class CommandRegister {

    ArrayList<BaseCommand> commands;
    private final NetWatchdog netWatchdog;

    public CommandRegister(NetWatchdog netWatchdog) {
        commands = new ArrayList<>();
        this.netWatchdog = netWatchdog;
        registerCommands();
    }

    /**
     * Tries to get command by name and call it.
     *
     * @param command   {@link String} name of command.
     * @return          <code>true</code> if command has been called;
     *                  <code>false</code> if no command was found.
     */
    public boolean evaluateCommand(String command) {
        String[] parts = command.split(" ");
        if (parts.length == 0)
            return false;

        BaseCommand c = getCommandByName(parts[0]);
        if (c != null) {
            c.call(Arrays.copyOfRange(parts, 1, parts.length));
            return true;
        }
        return false;
    }

    /**
     * Initializes and registers all available commands.
     */
    private void registerCommands() {
        registerCommand(new ExitCommand(netWatchdog));
        registerCommand(new ComponentCommand(netWatchdog));
        registerCommand(new ComponentsCommand(netWatchdog));
        registerCommand(new HelpCommand(netWatchdog));
        registerCommand(new ClearCommand(netWatchdog));
        registerCommand(new AppCommand(netWatchdog));
    }

    /**
     * Add a command object to command register if its name is
     * not in use already.
     *
     * @param command   {@link BaseCommand} to add.
     * @return          <code>true</code> if command has been added;
     *                  <code>false</code> if command with same name
     *                  is already registered.
     */
    public boolean registerCommand(BaseCommand command) {
        if (getCommandByName(command.getName()) != null)
            return false;
        commands.add(command);
        return true;
    }

    /**
     * Looks for and returns command which matches the
     * name to look for.
     *
     * @param name      {@link String} name to look for.
     * @return          {@link BaseCommand} which's name
     *                  matches <code>name</code> parameter.
     */
    public BaseCommand getCommandByName(String name) {
        if (name == null || name.length() == 0)
            return null;

        for (BaseCommand c : commands) {
            if (c.getName().equalsIgnoreCase(name))
                return c;
        }
        return null;
    }

    /**
     * Returns JLine argument completer.
     *
     * @return  {@link ArgumentCompleter} containing all command
     *          names.
     */
    public ArgumentCompleter getCommandNameArgumentCompleter() {
        String[] names = new String[commands.size()];
        int i = 0;
        for (BaseCommand c : commands) {
            names[i] = c.getName();
            i++;
        }
        return new ArgumentCompleter(new StringsCompleter(names));
    }

    public ArrayList<BaseCommand> getCommands() {
        return commands;
    }
}
