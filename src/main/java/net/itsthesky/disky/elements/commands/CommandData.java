package net.itsthesky.disky.elements.commands;

public class CommandData {
    private String name;
    private CommandObject command;

    public CommandData(String name, CommandObject command) {
        this.name = name;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public CommandObject getCommand() {
        return command;
    }
}
