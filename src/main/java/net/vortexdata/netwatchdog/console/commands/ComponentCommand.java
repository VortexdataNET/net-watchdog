package net.vortexdata.netwatchdog.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.CLI;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentCommand extends BaseCommand {

    public ComponentCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "component", "Configure and set up components.");
        this.args.put("create [name]", "Creates a new target.");
        this.args.put("disable", "Disables a target.");
        this.args.put("enable", "Enables a target.");
        this.args.put("delete", "Deletes a target (must be disabled).");
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
                            InputStream headIs = getClass().getResourceAsStream("/component-template.conf");
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

            } else if (args[0].equalsIgnoreCase("enable")) {
                if (args.length > 1) {
                    CLI.print("Trying to load component from file " + args[1] + "...");
                    netWatchdog.getComponentManager().enableComponent(args[1], true);
                } else {
                    CLI.print("Please specify the components filename.");
                }
            } else if (args[0].equalsIgnoreCase("delete")) {

            } else {
                printUsage();
            }
        } else {
            printUsage();
        }
    }

}
