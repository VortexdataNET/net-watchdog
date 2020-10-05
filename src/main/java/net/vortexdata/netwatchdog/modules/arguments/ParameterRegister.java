package net.vortexdata.netwatchdog.modules.arguments;

import net.vortexdata.netwatchdog.NetWatchdog;
import net.vortexdata.netwatchdog.modules.arguments.args.ParamBase;
import net.vortexdata.netwatchdog.modules.arguments.args.ParamLoglevel;

import java.util.ArrayList;

public class ParameterRegister {

    private String[] args;
    private ArrayList<ParamBase> params;
    private NetWatchdog netWatchdog;

    public ParameterRegister(String[] args, NetWatchdog netWatchdog) {
        this.netWatchdog = netWatchdog;
        this.args = args;
        this.params = new ArrayList<>();

        params.add(new ParamLoglevel());
    }

    public void evaluateArguments() {

        if (args.length == 0) {
            netWatchdog.getLogger().debug("No arguments found, skipping evaluation.");
            return;
        } else {
            netWatchdog.getLogger().debug("Found " + args.length + " arguments.");
        }

        String calledName = "";
        ParamBase pb = null;
        ArrayList<String> pbArgs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                pbArgs.add(args[i]);
            } else {
                if (pb != null) {
                    pb.runPreparation(pbArgs.toArray(new String[pbArgs.size()]), calledName, netWatchdog);
                    calledName = "";
                    pb = null;
                    pbArgs.clear();
                }
                if (args[i].startsWith("--")) {
                    calledName = args[i].substring(2);
                    pb = getParamFromFull(calledName);
                } else if (args[i].startsWith("-")) {
                    calledName = args[i].substring(1);
                    pb = getParamFromShort(calledName);
                }
            }
            if (i == args.length-1 && pb != null)
                pb.runPreparation(pbArgs.toArray(new String[pbArgs.size()]), calledName, netWatchdog);
        }
    }

    private ParamBase getParamFromShort(String shortName) {
        for (ParamBase pb : params)
            if (pb.getShortName().equalsIgnoreCase(shortName))
                return pb;
        return null;
    }

    private ParamBase getParamFromFull(String fullName) {
        for (ParamBase pb : params)
            if (pb.getFullName().equalsIgnoreCase(fullName))
                return pb;
        return null;
    }

}
