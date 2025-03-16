package net.itsthesky.disky.elements.structures.slash.models;

import ch.njol.skript.lang.Trigger;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.JDAUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.itsthesky.disky.elements.events.interactions.SlashCompletionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsedCommand {

    private List<ParsedArgument> arguments = new ArrayList<>();

    private String description;
    private Map<DiscordLocale, String> descriptionLocalizations = new HashMap<>();

    private String originalName; // Store the original command path
    private String name;
    private Map<DiscordLocale, String> nameLocalizations = new HashMap<>();

    private List<Permission> enabledFor = new ArrayList<>();
    private boolean disabledByDefault = false;

    private String rawBot; // it's bot's name
    private Bot bot;
    private List<String> guilds = new ArrayList<>();

    private long cooldown; // in ms
    private Trigger onCooldown;

    private Trigger trigger;

    // ------------------------------------------------------------

    public String getOriginalName() {
        return originalName != null ? originalName : name;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

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

    public String getRawBot() {
        return rawBot;
    }

    public void setRawBot(String rawBot) {
        this.rawBot = rawBot;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public Trigger getOnCooldown() {
        return onCooldown;
    }

    public void setOnCooldown(Trigger onCooldown) {
        this.onCooldown = onCooldown;
    }

    public boolean hasCooldown() {
        return cooldown > 0 || onCooldown != null;
    }

    public boolean shouldUpdate(ParsedCommand command) {
        return true;
    }

    public void prepareArguments(SlashCommandInteractionEvent event) {
        final var options = event.getOptions();
        DiSky.debug("Found '" + arguments.size() + " args' for '" + options.size() + " options'");
        for (ParsedArgument argument : arguments) {
            final OptionMapping option = options.stream().filter(opt -> opt.getName().equals(argument.getName())).findFirst().orElse(null);
            if (option == null) {
                argument.setValue(null);
                continue;
            }

            if (argument.getCustomArgument() != null) {
                final var customArgument = argument.getCustomArgument();
                final var value = customArgument.convert(event, option);

                argument.setValue(value);
            } else {
                argument.setValue(JDAUtils.parseOptionValue(option));
            }
        }
    }

    public void prepareArguments(CommandAutoCompleteInteractionEvent event) {
        final var options = event.getOptions();
        DiSky.debug("Found '" + arguments.size() + " args' for '" + options.size() + " options'");
        for (ParsedArgument argument : arguments) {
            final OptionMapping option = options.stream().filter(opt -> opt.getName().equals(argument.getName())).findFirst().orElse(null);
            if (option == null) {
                argument.setValue(null);
                continue;
            }

            if (argument.getCustomArgument() != null) {
                final var customArgument = argument.getCustomArgument();
                final var value = customArgument.convert(event, option);

                argument.setValue(value);
            } else {
                argument.setValue(JDAUtils.parseOptionValue(option));
            }
        }
    }
}
