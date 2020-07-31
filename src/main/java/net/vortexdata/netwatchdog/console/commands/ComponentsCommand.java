package net.vortexdata.netwatchdog.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.console.CLI;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.component.ComponentManager;
import net.vortexdata.netwatchdog.utils.DateUtils;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.File;
import java.util.ArrayList;

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
            builder.append(String.format("%-32s%-32s", "Name", "Status") + "\n")
            .append("--------------------------------------------------\n\n");

            for (BaseComponent c : netWatchdog.getComponentManager().getComponents()) {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                        .append(String.format("%-32s%-32s", c.getName(), "loaded") + "\n");
            }
            for (File f : netWatchdog.getComponentManager().getUnloadedComponents()) {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                        .append(String.format("%-32s%-32s", f.getName().split("-")[0], "not loaded") + "\n");
            }
            CLI.print(builder.toAnsi());
        }
    }

}
