package net.itsthesky.disky.elements.structures.context;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.events.rework.CommandEvents;
import net.itsthesky.disky.elements.structures.slash.BotReadyWaiter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StructContextCommand extends Structure {

    public static final Priority PRIORITY = new Priority(800);
    private static final Pattern LIST = Pattern.compile("\\s*,\\s*/?");

    private static final EntryValidator VALIDATOR = EntryValidator.builder()
            .addEntry("bot", "", false)
            .addSection("trigger", false)

            .addEntry("guilds", "", true)
            .addEntry("enabled for", "all", true)
            .addSection("name", true)

            .build();

    static {
        // Register both user and message command structures
        Skript.registerStructure(
                StructContextCommand.class,
                VALIDATOR,
                "user command %string%",
                "message command %string%"
        );
    }

    private ParsedContextCommand parsedCommand;
    private EntryContainer entryContainer;
    private Node structure;

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult, @NotNull EntryContainer entryContainer) {
        this.entryContainer = entryContainer;
        this.structure = entryContainer.getSource();

        // Initialize the command with the correct type based on the matched pattern
        Command.Type type = matchedPattern == 0 ? Command.Type.USER : Command.Type.MESSAGE;
        parsedCommand = new ParsedContextCommand(type);

        // Get the command name from the first argument
        String name = args[0] == null ? null : (String) args[0].getSingle();
        if (name == null) {
            Skript.error("Command name cannot be null");
            return false;
        }
        parsedCommand.setName(name);

        return true;
    }

    @Override
    public boolean load() {
        // Parse name localizations
        if (!parseName()) {
            return false;
        }

        // Parse bot and guilds
        if (!parsePlaces()) {
            return false;
        }

        // Parse trigger
        if (!parseTrigger()) {
            return false;
        }

        // Parse permissions
        if (!parsePermissions()) {
            return false;
        }

        // Debug information
        DiSky.debug("------------------- Name -------------------");
        DiSky.debug("Default: " + parsedCommand.getName());
        for (DiscordLocale locale : parsedCommand.getNameLocalizations().keySet()) {
            DiSky.debug(" - Locale: " + locale + " | Value: " + parsedCommand.getNameLocalizations().get(locale));
        }
        if (parsedCommand.getNameLocalizations().isEmpty()) {
            DiSky.debug("No localizations found.");
        }

        DiSky.debug("------------------- Places -------------------");
        DiSky.debug("Pre-bot (name): " + parsedCommand.getRawBot());
        for (String guild : parsedCommand.getGuilds()) {
            DiSky.debug("- Guild: " + guild);
        }

        DiSky.debug("------------------- Trigger -------------------");
        if (parsedCommand.getTrigger() != null) {
            DiSky.debug("Trigger found.");
            DiSky.debug(" - Label: " + parsedCommand.getTrigger().getDebugLabel());
        } else {
            DiSky.debug("No trigger found.");
        }

        DiSky.debug("------------------- Permissions -------------------");
        DiSky.debug("Enabled for: " + (parsedCommand.getPermissions().getPermissionsRaw() == null ? "all" : Permission.getPermissions(parsedCommand.getPermissions().getPermissionsRaw())));

        // Register the command
        final var bot = DiSky.getManager().getBotByName(parsedCommand.getRawBot());
        if (bot != null && bot.getInstance().getStatus() == net.dv8tion.jda.api.JDA.Status.CONNECTED) {
            bot.getContextManager().registerCommand(parsedCommand);
        } else {
            BotReadyWaiter.WaitingContextCommands.computeIfAbsent(parsedCommand.getRawBot(), k -> new ArrayList<>()).add(parsedCommand);
        }

        return true;
    }

    private boolean parseName() {
        final SectionNode sectionNode = entryContainer.getOptional("name", SectionNode.class, true);
        if (sectionNode == null) {
            return true;
        }

        sectionNode.convertToEntries(0);
        final Map<DiscordLocale, String> localizations = new HashMap<>();

        for (Node node : sectionNode) {
            final String key = node.getKey();
            final String value = sectionNode.get(key, "");
            if (value.isEmpty()) {
                Skript.error("Empty localization value for key: " + key);
                return false;
            }

            final DiscordLocale locale = DiscordLocale.from(key.equals("en") ? "en-US" : key);
            if (locale == DiscordLocale.UNKNOWN) {
                Skript.error("Invalid locale key: " + key + " (Available: " + Arrays.toString(DiscordLocale.values()) + ")");
                return false;
            }

            localizations.put(locale, value);
        }

        parsedCommand.setNameLocalizations(localizations);
        return true;
    }

    private boolean parsePlaces() {
        final String rawBot = entryContainer.getOptional("bot", String.class, true);
        final String rawGuilds = entryContainer.getOptional("guilds", String.class, true);
        
        if (rawBot == null || rawBot.isEmpty()) {
            Skript.error("You must specify a bot for the command.");
            return false;
        }

        parsedCommand.setRawBot(rawBot);

        if (rawGuilds != null && !rawGuilds.isEmpty()) {
            final String[] guildIds = rawGuilds.split(LIST.pattern());
            for (String guildId : guildIds) {
                if (!guildId.matches("\\d+")) {
                    Skript.error("Invalid guild ID: " + guildId);
                    return false;
                }
                parsedCommand.addGuild(guildId);
            }
        }

        return true;
    }

    private boolean parseTrigger() {
        final SectionNode sectionNode = entryContainer.getOptional("trigger", SectionNode.class, true);
        if (sectionNode == null) {
            Skript.error("You must specify a trigger section for the command.");
            return false;
        }

        final var builtEvent = parsedCommand.getType() == Command.Type.USER
                ? CommandEvents.USER_COMMAND_EVENT
                : CommandEvents.MESSAGE_COMMAND_EVENT;
        final Class<? extends Event> eventClass = builtEvent.getBukkitEventClass();

        final Trigger trigger = new Trigger(
                getParser().getCurrentScript(),
                "on " + parsedCommand.getType().name().toLowerCase() + " command " + parsedCommand.getName(),
                builtEvent.createDiSkyEvent(),
                SkriptUtils.loadCode(sectionNode, eventClass)
        );

        parsedCommand.setTrigger(trigger);
        return true;
    }

    private boolean parsePermissions() {
        final String rawPermissions = entryContainer.getOptional("enabled for", String.class, true);
        if (rawPermissions == null) {
            Skript.error("You must specify the permissions for the command.");
            return false;
        }

        final DefaultMemberPermissions permissions;
        if (rawPermissions.equalsIgnoreCase("all") || rawPermissions.equalsIgnoreCase("enabled"))
            permissions = DefaultMemberPermissions.ENABLED;
        else if (rawPermissions.equalsIgnoreCase("none") || rawPermissions.equalsIgnoreCase("disabled"))
            permissions = DefaultMemberPermissions.DISABLED;
        else
            permissions = DefaultMemberPermissions.enabledFor(Arrays.stream(LIST.split(rawPermissions))
                    .map(raw -> Permission.valueOf(raw.toUpperCase().replace(" ", "_")))
                    .toArray(Permission[]::new));

        parsedCommand.setPermissions(permissions);
        return true;
    }

    @Override
    public @NotNull Priority getPriority() {
        return PRIORITY;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "Context Command Structure";
    }
}