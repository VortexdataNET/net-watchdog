package net.vortexdata.netwatchdog.modules.console.commands;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.boothandler.Boothandler;
import net.vortexdata.netwatchdog.modules.component.BaseComponent;
import net.vortexdata.netwatchdog.modules.console.cli.CLI;
import net.vortexdata.netwatchdog.modules.updater.UpdateManager;
import net.vortexdata.netwatchdog.utils.GithubAPIUtils;
import net.vortexdata.netwatchdog.utils.VersionUtils;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AppCommand extends BaseCommand {

    public AppCommand(NetWatchdog netWatchdog) {
        super(netWatchdog, "app", "Lets you control the app.");
        this.args.put("upgrade [<verstiontag>]", "Upgrades your app to a newer version.");
        this.args.put("info", "Displays some information about the app.");
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("upgrade")) {
                if (args.length > 1)
                    if (VersionUtils.isVersionTagValid(args[1]) && netWatchdog.getUpdateManager().isTagAvailable(args[1]))
                        upgrade(args[1]);
                    else
                        CLI.print("Please enter a valid version tag.");
                else
                    upgrade(null);
            } else if (args[0].equalsIgnoreCase("info")) {
                printInfo();
            } else {
                printUsage();
            }
        } else {
            printInfo();
        }
    }

    private void printInfo() {
        String format = "%-20s%-24s";
        AttributedStringBuilder builder = new AttributedStringBuilder();
        builder.append(String.format(format, "Version", netWatchdog.getAppInfo().getVersionName())).append("\n");
        builder.append(String.format(format, "RAM Usage (mb)", (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1000)).append("\n");
        builder.append(String.format(format, "Uptime (hours)", Boothandler.getBootStart().until(LocalDateTime.now(), ChronoUnit.HOURS))).append("\n");
        CLI.print(builder.toAnsi());
    }

    private void upgrade(String version) {

        if (!CLI.promptYesNo("Are you sure you want to look for upgrades?")) {
            CLI.print("Aborting...");
            return;
        }


        CLI.print("Fetching update information... Please wait. \n\n");
        if (version == null) {
            version = netWatchdog.getUpdateManager().getLatestVersionTag();
            if (VersionUtils.compareVersionTags(version, netWatchdog.getAppInfo().getVersionName()) == 0) {
                CLI.print("You are already running the latest version.");
                return;
            }
        } else if (VersionUtils.compareVersionTags(version, netWatchdog.getAppInfo().getVersionName()) == -1) {
            CLI.print("You can not downgrade to an older version as this may break some systems.");
            return;
        } else if (VersionUtils.compareVersionTags(version, netWatchdog.getAppInfo().getVersionName()) == 0) {
            CLI.print("You are already running this version.");
            return;
        }



        JSONObject upgradeReleaseInfo = netWatchdog.getUpdateManager().getReleaseInfo(version);
        JSONObject currentReleaseInfo = netWatchdog.getUpdateManager().getReleaseInfo(netWatchdog.getAppInfo().getVersionName());

        String format = "%-32s%-24s%-24s";
        AttributedStringBuilder builder = new AttributedStringBuilder();
        builder.append(String.format(format, "", "Current", "Upgrade")).append("\n");
        builder.append("--------------------------------------------------------------------------------").append("\n");
        builder.append(String.format(format, "JAR size", GithubAPIUtils.getJarAssetInfo(currentReleaseInfo).getDouble("size") / 1000000.00 + " mb", GithubAPIUtils.getJarAssetInfo(upgradeReleaseInfo).getDouble("size") / 1000000.00 + " mb")).append("\n");
        builder.append(String.format(format, "Version Name", currentReleaseInfo.getString("name"), upgradeReleaseInfo.getString("name"))).append("\n");
        builder.append(String.format(format, "Version Tag", currentReleaseInfo.getString("tag_name"), upgradeReleaseInfo.getString("tag_name"))).append("\n");
        builder.append(String.format(format, "Release Date", currentReleaseInfo.getString("published_at"), upgradeReleaseInfo.getString("published_at"))).append("\n");
        CLI.print(builder.toAnsi());

        CLI.print("\nCurrent release info: " + currentReleaseInfo.getString("html_url"));
        CLI.print("Upgrade release info: " + upgradeReleaseInfo.getString("html_url") + "\n\n");

        if (!CLI.promptDangerous("Are you sure you want to upgrade to the new version? This may cause compatibility issues.")) {
            CLI.print("Aborting...");
            return;
        }

        CLI.print("Downloading and applying upgrade...");

        if (netWatchdog.getUpdateManager().downloadRelease(upgradeReleaseInfo.getString("tag_name"))) {

            File releaseFile = new File(UpdateManager.getDownloadedReleasePath(upgradeReleaseInfo.getString("tag_name")));
            if (releaseFile.renameTo(new File("net-watchdog-" + upgradeReleaseInfo.getString("tag_name") + ".jar"))) {
                CLI.print("New version jar file has been moved to app root directory. Please consider adjusting your launch script to use the new file name.");
            } else {
                CLI.print("Failed to move new version jar file to app root. Please manually move it from " + UpdateManager.getDownloadedReleasePath(upgradeReleaseInfo.getString("tag_name")) + " into your projects root directory and adjust your launch script (if using one).");
            }

            if (CLI.promptYesNo("Do you want to shut down the app to update now?"))
                netWatchdog.shutdown();

        } else {
            CLI.print("Failed to download new release. Please try again or manually download it from the projects GitHub page.");
        }

    }

}
