package info.itsthesky.disky.elements.structures.slash.models;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class RegisteredCommand extends ParsedCommand {

    private final long commandId;
    private final String botName;
    private final String guildId;

    private final Map<User, Long> cooldowns;

    public RegisteredCommand(ParsedCommand parsedCommand,
                             long commandId,
                             String botName,
                             String guildId) {
        setArguments(parsedCommand.getArguments());
        setDescription(parsedCommand.getDescription());
        setName(parsedCommand.getName());
        setDescriptionLocalizations(parsedCommand.getDescriptionLocalizations());
        setEnabledFor(parsedCommand.getEnabledFor());
        setNameLocalizations(parsedCommand.getNameLocalizations());
        setDisabledByDefault(parsedCommand.isDisabledByDefault());
        setTrigger(parsedCommand.getTrigger());
        setBot(parsedCommand.getBot());
        setOnCooldown(parsedCommand.getOnCooldown());
        setCooldown(parsedCommand.getCooldown());

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
        return cooldowns.containsKey(user) && cooldowns.get(user) > System.currentTimeMillis();
    }

    public long getCooldown(User user) {
        return cooldowns.getOrDefault(user, 0L);
    }

    public void setCooldown(User user) {
        cooldowns.put(user, System.currentTimeMillis() + getCooldown());
    }
}
