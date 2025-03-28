package net.itsthesky.disky.elements.structures.context;

import ch.njol.skript.lang.Trigger;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.itsthesky.disky.elements.events.rework.CommandEvents;
import org.bukkit.event.Event;

import java.util.*;

public class ParsedContextCommand {
    private final Command.Type type;
    private String name;
    private Map<DiscordLocale, String> nameLocalizations;
    private List<String> guilds;
    private String rawBot;
    private DefaultMemberPermissions permissions;
    private Trigger trigger;

    public ParsedContextCommand(Command.Type type) {
        this.type = type;
        this.nameLocalizations = new HashMap<>();
        this.guilds = new ArrayList<>();
    }

    public Event wrapEvent(Object event) {
        if (event instanceof final UserContextInteractionEvent jdaEvent) {
            return CommandEvents.USER_COMMAND_EVENT.createBukkitInstance(jdaEvent);
        } else if (event instanceof final MessageContextInteractionEvent jdaEvent) {
            return CommandEvents.MESSAGE_COMMAND_EVENT.createBukkitInstance(jdaEvent);
        }

        throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
    }

    // Getters & Setters
    public Command.Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<DiscordLocale, String> getNameLocalizations() {
        return nameLocalizations;
    }

    public void setNameLocalizations(Map<DiscordLocale, String> nameLocalizations) {
        this.nameLocalizations = nameLocalizations;
    }

    public List<String> getGuilds() {
        return guilds;
    }

    public void addGuild(String guildId) {
        this.guilds.add(guildId);
    }

    public String getRawBot() {
        return rawBot;
    }

    public void setRawBot(String rawBot) {
        this.rawBot = rawBot;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public DefaultMemberPermissions getPermissions() {
        return permissions;
    }

    public void setPermissions(DefaultMemberPermissions permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedContextCommand that = (ParsedContextCommand) o;
        return type == that.type &&
                Objects.equals(name, that.name) &&
                Objects.equals(guilds, that.guilds) &&
                Objects.equals(rawBot, that.rawBot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, guilds, rawBot);
    }

    @Override
    public String toString() {
        return "ParsedContextCommand{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", guilds=" + guilds +
                ", bot='" + rawBot + '\'' +
                '}';
    }
}