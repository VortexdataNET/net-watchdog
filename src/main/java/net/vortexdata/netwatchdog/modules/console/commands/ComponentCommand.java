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

package net.vortexdata.netwatchdog.modules.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Component modifier command.
 *
 * @author  Sandro Kierner
 * @since 0.0.1
 * @version 0.2.0
 */
public class ComponentCommand extends BaseCommand {

    public ComponentCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "component", "Configure and set up components.");
        this.args.put("create [name]", "Creates a new target.");
        this.args.put("disable [name]", "Disables a target.");
        this.args.put("enable [filename]", "Enables a target.");
        this.args.put("delete [filename]", "Deletes a target (must be disabled).");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length > 1) {
                    String newFilePath = ComponentManager.COMPONENTS_DIR + args[1] + ComponentManager.COMPONENT_IDENTIFIER;
                    try {
                        BufferedReader headBr = null;
                        File file = new File(newFilePath);
                        if (file.exists()) {
                            if (args.length <= 2 || !args[2].equalsIgnoreCase("--force")) {
                                CLI.print("Component file " + newFilePath + " already exists. Use 'component create " + args[1] + " --force' to override existing.");
                                return;
                            }
                        }
                        BufferedWriter bw = new BufferedWriter(new FileWriter(newFilePath, false));
                        try {
                            InputStream headIs = getClass().getResourceAsStream("/rest-component.conf");
                            headBr = new BufferedReader(new InputStreamReader(headIs));
                            while (headBr.ready()) {
                                String line = headBr.readLine();
                                line = line.replace("%FILENAME%", args[1]);
                                bw.write(line + "\n");
                            }
                            headBr.close();
                            CLI.print("New component " + args[1] + " created at " + ComponentManager.COMPONENTS_DIR + args[1] + ComponentManager.COMPONENT_IDENTIFIER + ".");
                        } catch (Exception e) {
                            return;
                        }
                        bw.close();
                    } catch (Exception e) {
                        CLI.print("Can not create component. Please only use alpha-numeric characters. Appending error information: " + e.getMessage());
                    }
                } else {
                    printUsage();
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (args.length > 1) {
                    if (netWatchdog.getComponentManager().disableComponent(args[1]))
                        CLI.print("Component disabled.");
                    else
                        CLI.print("Component could not be disabled (is it loaded?).");
                } else {
                    printUsage();
                }
            } else if (args[0].equalsIgnoreCase("enable")) {
                if (args.length > 1) {
                    CLI.print("Trying to load component from file " + args[1] + "...");
                    netWatchdog.getComponentManager().enableComponent(args[1], true);
                } else {
                    CLI.print("Please specify the components filename.");
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length > 1) {
                    try {
                        Files.delete(Paths.get(ComponentManager.COMPONENTS_DIR + args[1] + ComponentManager.COMPONENT_IDENTIFIER));
                        CLI.print("Component file " + args[1] + " successfully deleted. Please bear in mind that the component may still be loaded. To unload, use 'component disable [name]'.");
                    } catch (IOException e) {
                        CLI.print("Could not delete component file " + args[1] + ": " + e.getMessage());
                    }
                } else {
                    printUsage();
                }
            } else {
                printUsage();
            }
        } else {
            printUsage();
        }
    }

}
