package net.vortexdata.netwatchdog.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.CLI;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;

import java.io.*;

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
                    try {
                        BufferedReader headBr = null;
                        CLI.print(ComponentManager.COMPONENTS_DIR + args[1] + ComponentManager.COMPONENT_IDENTIFIER);
                        BufferedWriter bw = new BufferedWriter(new FileWriter(ComponentManager.COMPONENTS_DIR + args[1] + ComponentManager.COMPONENT_IDENTIFIER));
                        try {
                            InputStream headIs = getClass().getResourceAsStream("/component-template.conf");
                            headBr = new BufferedReader(new InputStreamReader(headIs));
                            while (headBr.ready()) {
                                bw.write(headBr.readLine() + "\n");
                            }
                            headBr.close();
                        } catch (Exception e) {
                            return;
                        }
                        bw.close();
                    } catch (Exception e) {
                        CLI.print("Can not create component. Please only use alpha-numeric characters. Appending error information: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    printUsage();
                }
            } else if (args[0].equalsIgnoreCase("disable")) {

            } else if (args[0].equalsIgnoreCase("enable")) {
                if (args.length > 1) {
                    CLI.print("Trying to load component from file " + args[2] + "...");
                    netWatchdog.getComponentManager().enableComponent(args[2]);
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
