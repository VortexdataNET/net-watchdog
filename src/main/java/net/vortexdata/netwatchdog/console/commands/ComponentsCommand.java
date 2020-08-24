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
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.File;

/**
 * Component list and reload command.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.0.1
 */
public class ComponentsCommand extends BaseCommand {

    public ComponentsCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "components", "List all currently loaded components.");
        this.args.put("reload", "Reloads and re-initializes all components.");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                netWatchdog.getComponentManager().loadAll();
            } else {
                printUsage();
            }
        } else {

            if (netWatchdog.getComponentManager().getComponents().size() == 0 && netWatchdog.getComponentManager().getUnloadedComponents().size() == 0) {
                CLI.print("There are no components to show.");
                return;
            }


            StringBuilder sb = new StringBuilder();
            AttributedStringBuilder builder = new AttributedStringBuilder();
            builder.append(String.format("%-32s%-32s%-32s", "Filename", "Custom Name", "Status") + "\n")
            .append("----------------------------------------------------------------------------\n\n");

            for (BaseComponent c : netWatchdog.getComponentManager().getComponents()) {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                        .append(String.format("%-32s%-32s%-32s", c.getFilename(), c.getName(), "loaded") + "\n");
            }
            for (File f : netWatchdog.getComponentManager().getUnloadedComponents()) {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                        .append(String.format("%-32s%-32s%-32s", f.getName().split("-")[0], "N/A", "not loaded") + "\n");
            }
            CLI.print(builder.toAnsi());
        }
    }

}
