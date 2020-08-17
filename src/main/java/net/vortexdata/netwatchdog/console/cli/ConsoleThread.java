package net.vortexdata.netwatchdog.console.cli;

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
            String input = "";
            try {
                input = CLI.readLine("> ");
                if (!commandRegister.evaluateCommand(input))
                    CLI.print(input.split(" ")[0] + ": Command not found");
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public void end() {
        active = false;
    }

}
