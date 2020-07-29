package net.vortexdata.netwatchdog.console;

public class ConsoleThread extends Thread {

    private CommandRegister commandRegister;
    private boolean active;

    public ConsoleThread(CommandRegister commandRegister) {
        active = true;
        this.commandRegister = commandRegister;
    }

    @Override
    public void run() {
        active = true;
        while (active) {
            commandRegister.evaluateCommand(CLI.readLine("> "));
        }
    }

    public void end() {
        active = false;
    }

}
