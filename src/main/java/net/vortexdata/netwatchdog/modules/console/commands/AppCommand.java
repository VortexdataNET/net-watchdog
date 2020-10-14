package net.vortexdata.netwatchdog.modules.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import net.vortexdata.netwatchdog.utils.VersionUtils;

public class AppCommand extends BaseCommand {

    public AppCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "app", "Lets you control the app.");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("upgrade")) {
                if (args.length > 2)
                    if (VersionUtils.isVersionTagValid(args[1]) && netWatchdog.getUpdateManager().isTagAvailable(args[1]))
                        upgrade(args[1]);
                    else
                        CLI.print("Please enter a valid version tag.");
                else
                    upgrade(null);
            } else if (args[0].equalsIgnoreCase("info")) {

            } else if (args[0].equalsIgnoreCase("list")) {

            }
        } else {
            printUsage();
        }
    }

    private void upgrade(String version) {
        if (version == null) {
            version = netWatchdog.getUpdateManager().getLatestVersionTag();
            if (VersionUtils.compareVersionTags(version, netWatchdog.getAppInfo().getVersionName()) == 0) {
                CLI.print("You are already running the latest version.");
                return;
            }
        } else if (VersionUtils.compareVersionTags(version, netWatchdog.getAppInfo().getVersionName()) == -1) {
            CLI.print("You can not downgrade to an old app version as this may break some systems.");
            return;
        } else if (VersionUtils.compareVersionTags(version, netWatchdog.getAppInfo().getVersionName()) == 0) {
            CLI.print("You are already running this version.");
            return;
        }

        CLI.print("Release URL: " + );
    }

}
