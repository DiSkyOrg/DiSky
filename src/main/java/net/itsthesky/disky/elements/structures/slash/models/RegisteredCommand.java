package net.itsthesky.disky.elements.structures.slash.models;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisteredCommand {

    private ParsedCommand parsedCommand;

    private List<ParsedCommand> subCommands;

    private final long commandId;
    private final String botName;
    private final String guildId;

    private final Map<User, Long> cooldowns;

    public RegisteredCommand(ParsedCommand parsedCommand,
                             long commandId,
                             String botName,
                             String guildId) {
        this.parsedCommand = parsedCommand;

        this.commandId = commandId;
        this.botName = botName;
        this.guildId = guildId;
        this.cooldowns = new HashMap<>();
    }

    public long getCommandId() {
        return commandId;
    }

    public String getBotName() {
        return botName;
    }

    public String getGuildId() {
        return guildId;
    }

    public boolean isInCooldown(User user) {
        Long cooldownTime = cooldowns.get(user);
        return cooldownTime != null && cooldownTime > System.currentTimeMillis();
    }

    public long getCooldown(User user) {
        return cooldowns.getOrDefault(user, 0L);
    }

    public void setCooldown(User user) {
        cooldowns.put(user, System.currentTimeMillis() + getParsedCommand().getCooldown());
    }

    public ParsedCommand getParsedCommand() {
        return parsedCommand;
    }

    public void setParsedCommand(ParsedCommand parsedCommand) {
        this.parsedCommand = parsedCommand;
    }

    public String getName() {
        return parsedCommand == null ? "" : parsedCommand.getName();
    }
}
