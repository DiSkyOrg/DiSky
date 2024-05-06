package info.itsthesky.disky.elements.structures.slash.models;

import ch.njol.skript.lang.Trigger;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.JDAUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedCommand {

    private List<ParsedArgument> arguments = new ArrayList<>();

    private String description;
    private Map<DiscordLocale, String> descriptionLocalizations = new HashMap<>();

    private String name;
    private Map<DiscordLocale, String> nameLocalizations = new HashMap<>();

    private List<Permission> enabledFor = new ArrayList<>();
    private boolean disabledByDefault = false;

    private Bot bot;
    private List<String> guilds = new ArrayList<>();

    private Trigger trigger;

    // ------------------------------------------------------------

    public List<ParsedArgument> getArguments() {
        return arguments;
    }

    public void setArguments(List<ParsedArgument> arguments) {
        this.arguments = arguments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<DiscordLocale, String> getDescriptionLocalizations() {
        return descriptionLocalizations;
    }

    public void setDescriptionLocalizations(Map<DiscordLocale, String> descriptionLocalizations) {
        this.descriptionLocalizations = descriptionLocalizations;
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

    public void setEnabledFor(List<Permission> enabledFor) {
        this.enabledFor = enabledFor;
    }

    public List<Permission> getEnabledFor() {
        return enabledFor;
    }

    public void setDisabledByDefault(boolean disabledByDefault) {
        this.disabledByDefault = disabledByDefault;
    }

    public boolean isDisabledByDefault() {
        return disabledByDefault;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public List<String> getGuilds() {
        return guilds;
    }

    public void addGuild(String guild) {
        guilds.add(guild);
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public boolean shouldUpdate(ParsedCommand command) {
        return !this.equals(command)
                || !this.getArguments().equals(command.getArguments())
                || !this.getDescription().equals(command.getDescription())
                || !this.getName().equals(command.getName())
                || !this.getEnabledFor().equals(command.getEnabledFor())
                || this.isDisabledByDefault() != command.isDisabledByDefault();
                /*|| !this.getBots().equals(command.getBots())
                || !this.getGuilds().equals(command.getGuilds());*/
    }

    public void prepareArguments(List<OptionMapping> options) {
        for (int i = 0; i < options.size(); i++) {
            final OptionMapping option = options.get(i);
            final ParsedArgument argument = arguments.get(i);

            argument.setValue(JDAUtils.parseOptionValue(option));
        }
    }
}
