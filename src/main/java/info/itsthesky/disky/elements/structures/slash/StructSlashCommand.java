package info.itsthesky.disky.elements.structures.slash;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Timespan;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.skript.entries.MutexEntryData;
import info.itsthesky.disky.api.skript.entries.SimpleKeyValueEntries;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.events.bots.ReadyEvent;
import info.itsthesky.disky.elements.events.interactions.SlashCommandReceiveEvent;
import info.itsthesky.disky.elements.events.interactions.SlashCompletionEvent;
import info.itsthesky.disky.elements.structures.slash.elements.OnCooldownEvent;
import info.itsthesky.disky.elements.structures.slash.models.ParsedArgument;
import info.itsthesky.disky.elements.structures.slash.models.ParsedCommand;
import info.itsthesky.disky.elements.structures.slash.models.ParsedGroup;
import info.itsthesky.disky.elements.structures.slash.models.SlashCommandInformation;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.KeyValueEntryData;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ItsThesky
 */
public class StructSlashCommand extends Structure {

    public static final Priority PRIORITY = new Priority(800);
    public static final Set<SlashCommandInformation> REMOVED_COMMANDS = new HashSet<>();

    private static final Pattern ARGUMENT = Pattern.compile("(\\[)?<(?<type>\\w+)=\"(?<name>\\w+)\">(\\])?");
    private static final Pattern STRUCTURE = Pattern.compile("slash command ([A-z]+)( )?(.+)?");
    private static final Pattern LIST = Pattern.compile("\\s*,\\s*/?");

    private static final EntryValidator CORE_VALIDATOR = EntryValidator.builder()

            .addEntryData(new MutexEntryData<>("description", "def", false,
                    SkriptUtils.custom(), String::valueOf))
            .addEntryData(SimpleKeyValueEntries.createList("enabled for", new ArrayList<>(), true,
                    value -> Permission.valueOf(value.toUpperCase().replace(" ", "_"))))
            .addEntryData(SimpleKeyValueEntries.createBooleanEntry("disabled", false, true))

            .addEntry("bot", "", true)
            .addEntry("guilds", "", true)

            .addSection("arguments", true)
            .addSection("name", true)

            .addEntry("group", "", true)

            .addEntryData(new KeyValueEntryData<Timespan>("cooldown", null, true) {
                @Override
                protected @Nullable Timespan getValue(@NotNull String value) {
                    return Timespan.parse(value);
                }
            })
            .addSection("on cooldown", true)

            .addSection("trigger", false)

            .build();

    private static final EntryValidator ARGUMENT_VALIDATOR = EntryValidator.builder()
            .addEntry("description", "", false)

            .addSection("choices", true)
            .addSection("on completion request", true)

            .build();

    static {
        Skript.registerStructure(
                StructSlashCommand.class,
                CORE_VALIDATOR,
                "slash command <([^\\s]+)( .+)?$>"
        );
    }

    private ParsedCommand parsedCommand;
    private EntryContainer entryContainer;
    private @NotNull Node structure;
    private Node node;

    public ParsedCommand getParsedCommand() {
        return parsedCommand;
    }

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult, @NotNull EntryContainer entryContainer) {
        this.entryContainer = entryContainer;
        structure = getEntryContainer().getSource();
        node = getParser().getNode();
        return true;
    }

    @Override
    public boolean load() {
        parsedCommand = new ParsedCommand();

        // Default command name
        final String commandName = parseCommandName();
        if (commandName == null)
            return false;
        parsedCommand.setName(commandName);


        // Arguments
        final List<ParsedArgument> arguments = parseArguments();
        if (arguments == null)
            return false;
        parsedCommand.setArguments(arguments);


        // Description & Name (localizations)
        final boolean description = parseDescription();
        if (!description)
            return false;
        final boolean name = parseName();
        if (!name)
            return false;


        // Meta
        parsedCommand.setEnabledFor(entryContainer.get("enabled for", List.class, true));
        parsedCommand.setDisabledByDefault(entryContainer.get("disabled", Boolean.class, true));


        // Registering places
        final boolean validPlaces = parsePlaces();
        if (!validPlaces)
            return false;


        // Trigger
        final boolean trigger = parseTrigger();
        if (!trigger)
            return false;


        // Cooldown
        final boolean cooldown = parseCooldown();
        if (!cooldown)
            return false;


        // Parent/group
        final boolean group = parseGroup();
        if (!group)
            return false;

        //region Debug
        DiSky.debug("------------------- Name -------------------");
        DiSky.debug("Default: " + parsedCommand.getName());
        for (DiscordLocale locale : parsedCommand.getNameLocalizations().keySet())
            DiSky.debug(" - Locale: " + locale + " | Value: " + parsedCommand.getNameLocalizations().get(locale));
        if (parsedCommand.getNameLocalizations().isEmpty())
            DiSky.debug("No localizations found.");

        DiSky.debug("------------------- Args (" + parsedCommand.getArguments().size() + ") -------------------");
        for (ParsedArgument arg : parsedCommand.getArguments()) {
            DiSky.debug("Argument: " + arg.getName() + " | Type: " + arg.getType() + " | Optional: " + arg.isOptional());
            if (arg.hasChoices()) {
                DiSky.debug(" - Choices (" + arg.getChoices().size() + "):");
                for (String choice : arg.getChoices().keySet()) {
                    DiSky.debug("   - " + choice + " | Value: " + arg.getChoices().get(choice));
                }
            }
            DiSky.debug(" - Description: " + arg.getDescription());
        }

        DiSky.debug("------------------- Description -------------------");
        DiSky.debug("Default: " + parsedCommand.getDescription());
        for (DiscordLocale locale : parsedCommand.getDescriptionLocalizations().keySet())
            DiSky.debug(" - Locale: " + locale + " | Value: " + parsedCommand.getDescriptionLocalizations().get(locale));
        if (parsedCommand.getDescriptionLocalizations().isEmpty())
            DiSky.debug("No localizations found.");

        DiSky.debug("------------------- Meta -------------------");
        DiSky.debug("Enabled for: " + parsedCommand.getEnabledFor());
        DiSky.debug("Disabled by default: " + parsedCommand.isDisabledByDefault());
        DiSky.debug("Group: " + (parsedCommand.getGroup() == null ? "None" : parsedCommand.getGroup().getName()));

        DiSky.debug("------------------- Places -------------------");
        DiSky.debug("Pre-bot (name): " + parsedCommand.getRawBot());
        for (String guild : parsedCommand.getGuilds())
            DiSky.debug("- Guild: " + guild);

        DiSky.debug("------------------- Trigger -------------------");
        if (parsedCommand.getTrigger() != null) {
            DiSky.debug("Trigger found.");
            DiSky.debug(" - Label: " + parsedCommand.getTrigger().getDebugLabel());
        }
        else
            DiSky.debug("No trigger found.");

        DiSky.debug("------------------- Cooldown -------------------");
        if (parsedCommand.hasCooldown()) {
            DiSky.debug("Cooldown: " + parsedCommand.getCooldown() + "ms (" + parsedCommand.getCooldown() / 1000 + "s)");
            if (parsedCommand.getOnCooldown() != null) {
                DiSky.debug(" - On Cooldown trigger found.");
                DiSky.debug("   - Label: " + parsedCommand.getOnCooldown().getDebugLabel());
            }
            else
                DiSky.debug(" - No on cooldown trigger found.");
        }
        else
            DiSky.debug("No cooldown found.");

        DiSky.debug("------------------- End -------------------");
        //endregion

        final var bot = DiSky.getManager().getBotByName(parsedCommand.getRawBot());
        if (bot == null)
            BotReadyWaiter.WaitingCommands.computeIfAbsent(parsedCommand.getRawBot(), k -> new ArrayList<>()).add(parsedCommand);
        else
            bot.getSlashManager().registerCommand(parsedCommand);

        REMOVED_COMMANDS.removeIf(info -> info.getCommand().equals(parsedCommand.getName()));
        return true;
    }

    @Override
    public void unload() {
        REMOVED_COMMANDS.add(new SlashCommandInformation(parsedCommand, node));
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "Slash Command Structure";
    }

    //region Arguments

    private List<ParsedArgument> parseArguments() {
        final List<ParsedArgument> arguments = new ArrayList<>();

        // First parse from the structure definition
        final String rawStructure = entryContainer.getSource().getKey();
        final Matcher argsMatcher = STRUCTURE.matcher(rawStructure.split("#")[0]);
        if (!argsMatcher.matches()) {
            Skript.error("Invalid structure pattern: " + entryContainer.getSource().getKey());
            return null;
        }

        final String rawArguments = argsMatcher.group(3);
        if (rawArguments == null)
            return arguments;

        final String[] args = rawArguments.split(" ");
        for (String arg : args) {
            final Matcher matcher = ARGUMENT.matcher(arg);
            if (!matcher.matches()) {
                Skript.error("Invalid argument pattern: " + arg);
                return null;
            }
            final String rawType = matcher.group("type");
            final String name = matcher.group("name");
            final boolean optional = arg.startsWith("[") && arg.endsWith("]");

            final OptionType type;
            try {
                type = OptionType.valueOf(rawType.toUpperCase());
            } catch (Exception ex) {
                Skript.error("Invalid argument type: " + rawType + " (Available: " + Arrays.toString(OptionType.values()) + ")");
                return null;
            }

            arguments.add(new ParsedArgument(type, name, !optional));
        }

        // Be sure there's no duplicated argument
        for (int i = 0; i < arguments.size(); i++) {
            final ParsedArgument arg = arguments.get(i);
            for (int j = i + 1; j < arguments.size(); j++) {
                final ParsedArgument other = arguments.get(j);
                if (arg.getName().equalsIgnoreCase(other.getName())) {
                    Skript.error("Duplicated argument name: " + arg.getName());
                    return null;
                }
            }
        }

        // Now parse from the entry container
        final SectionNode node = entryContainer.getOptional("arguments", SectionNode.class, true);
        if (node == null) {
            Skript.error("No arguments section found. Refer to the wiki for more information.");
            return null;
        }
        node.convertToEntries(0);

        for (final ParsedArgument argument : arguments) {
            final Node argNode = node.get(argument.getName());
            if (argNode == null)
                continue;

            if (argNode instanceof SectionNode) {
                final SectionNode argSection = (SectionNode) argNode;
                final EntryContainer container = ARGUMENT_VALIDATOR.validate(argSection);
                if (container == null)
                    return null;

                final String description = container.get("description", String.class, true);

                // Choices, if applicable
                final SectionNode choiceNode = container.getOptional("choices", SectionNode.class, true);
                if (choiceNode != null) {
                    final OptionType type = argument.getType();
                    if (!type.canSupportChoices()) {
                        Skript.error("Choices are not supported for the argument type: " + type);
                        return null;
                    }

                    choiceNode.convertToEntries(0);
                    for (Node choice : choiceNode) {
                        final String name = choice.getKey();
                        final String value = choiceNode.get(name, "");
                        if (value.isEmpty()) {
                            Skript.error("Empty value for choice: " + name);
                            return null;
                        }

                        final Object arg;
                        if (type.equals(OptionType.NUMBER) || type.equals(OptionType.INTEGER)) {
                            try {
                                arg = Integer.parseInt(value);
                            } catch (NumberFormatException ex) {
                                Skript.error("Invalid number value for choice: " + name);
                                return null;
                            }
                        } else if (type.equals(OptionType.STRING)) {
                            arg = value;
                        } else {
                            Skript.error("Invalid choice type: " + type);
                            return null;
                        }

                        argument.addChoice(name, arg);
                    }
                }

                //  auto completion
                final SectionNode completionNode = container.getOptional("on completion request", SectionNode.class, true);
                if (completionNode != null) {
                    final Trigger trigger = new Trigger(getParser().getCurrentScript(), "completion for argument " + argument.getName(), new SimpleEvent(),
                            SkriptUtils.loadCode(completionNode, SlashCompletionEvent.BukkitSlashCompletionEvent.class));
                    argument.setOnCompletionRequest(trigger);
                }

                if (argument.hasChoices() && argument.isAutoCompletion()) {
                    Skript.error("You can't have both auto completion and choices for the same argument.");
                    return null;
                }

                argument.setDescription(description);
            } else {
                final String description = node.getValue(argument.getName());
                argument.setDescription(description);
            }

        }

        return arguments;
    }

    //endregion

    //region Command Name

    public String parseCommandName() {
        final String rawStructure = entryContainer.getSource().getKey();
        final Matcher matcher = STRUCTURE.matcher(rawStructure.split("#")[0]);
        if (!matcher.matches()) {
            Skript.error("Invalid structure pattern: " + entryContainer.getSource().getKey());
            return null;
        }

        return matcher.group(1);
    }

    //endregion

    //region Localizations

    public static Map<DiscordLocale, String> parseLocalizations(SectionNode section) {
        section.convertToEntries(0);
        final Map<DiscordLocale, String> localizations = new HashMap<>();
        for (Node node : section) {
            final String key = node.getKey();
            final String value = section.get(key, "");
            if (value.isEmpty()) {
                Skript.error("Empty localization value for key: " + key);
                return null;
            }

            final DiscordLocale locale = DiscordLocale.from(key.equals("en") ? "en-US" : key);
            if (locale == DiscordLocale.UNKNOWN) {
                Skript.error("Invalid locale key: " + key + " (Available: " + Arrays.toString(DiscordLocale.values()) + ")");
                return null;
            }

            localizations.put(locale, value);
        }

        return localizations;
    }

    //endregion

    //region Description & Name

    public boolean parseDescription() {
        final MutexEntryData.MutexEntry<String> description = entryContainer.get("description", MutexEntryData.MutexEntry.class, true);
        if (description.isComplex()) {
            final EntryContainer subs = description.getEntryContainer();
            if (subs == null)
                return false;

            final Map<DiscordLocale, String> localizations = parseLocalizations(subs.getSource());
            if (localizations == null)
                return false;
            final String defaultDescription = localizations.get(DiscordLocale.ENGLISH_US);
            if (defaultDescription == null) {
                Skript.error("You must specify a default description for the command. (en-US)");
                return false;
            }

            parsedCommand.setDescriptionLocalizations(localizations);
            parsedCommand.setDescription(defaultDescription);
        } else {
            parsedCommand.setDescription(description.getValue());
        }

        return true;
    }

    public boolean parseName() {
        final SectionNode sectionNode = entryContainer.getOptional("name", SectionNode.class, true);
        if (sectionNode == null)
            return true;

        final Map<DiscordLocale, String> localizations = parseLocalizations(sectionNode);
        if (localizations == null)
            return false;

        parsedCommand.setNameLocalizations(localizations);
        return true;
    }

    //endregion

    //region Places

    public boolean parsePlaces() {
        final String rawBot = entryContainer.getOptional("bot", String.class, true);
        final String rawGuilds = entryContainer.getOptional("guilds", String.class, true);
        if ((rawBot == null || rawBot.isEmpty()) && (rawGuilds == null || rawGuilds.isEmpty())) {
            getParser().setNode(structure);
            Skript.error("You must specify at least one bot or guild ID.");
            return false;
        }

        if (rawBot == null || rawBot.isEmpty()) {
            getParser().setNode(structure);
            Skript.error("You must specify at least one bot.");
            return false;
        }

        parsedCommand.setRawBot(rawBot);

        if (!rawGuilds.isEmpty()) {
            final String[] guildIds = rawGuilds.split(LIST.pattern());
            for (String guildId : guildIds) {
                if (!guildId.matches("\\d+")) {
                    getParser().setNode(structure);
                    Skript.error("Invalid guild ID: " + guildId);
                    return false;
                }

                parsedCommand.addGuild(guildId);
            }
        }

        return true;
    }

    //endregion

    //region Trigger

    public boolean parseTrigger() {
        final SectionNode sectionNode = entryContainer.getOptional("trigger", SectionNode.class, true);
        if (sectionNode == null)
            return true;

        final Trigger trigger = new Trigger(getParser().getCurrentScript(), "on slash command " + parsedCommand.getName(), new ReadyEvent(),
                SkriptUtils.loadCode(sectionNode, SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent.class));
        parsedCommand.setTrigger(trigger);
        return true;
    }

    //endregion

    //region Cooldown

    public boolean parseCooldown() {
        final Timespan cooldown = entryContainer.getOptional("cooldown", Timespan.class, true);
        if (cooldown == null)
            return true;

        final SectionNode sectionNode = entryContainer.getOptional("on cooldown", SectionNode.class, true);
        if (sectionNode == null) {
            Skript.error("You must specify a section for the cooldown. ('on cooldown' section, to be ran when the command is on cooldown)");
            return false;
        }


        final Trigger trigger = new Trigger(getParser().getCurrentScript(), "on cooldown for " + parsedCommand.getName(),
                new OnCooldownEvent(),
                SkriptUtils.loadCode(sectionNode, OnCooldownEvent.BukkitCooldownEvent.class));

        parsedCommand.setCooldown(cooldown.getAs(Timespan.TimePeriod.MILLISECOND));
        parsedCommand.setOnCooldown(trigger);
        return true;
    }

    //endregion

    //region Group

    public boolean parseGroup() {
        final String group = entryContainer.getOptional("group", String.class, true);
        if (group == null || group.isEmpty())
            return true;

        final ParsedGroup parsedGroup = SlashGroupManager.getGroup(group);
        if (parsedGroup == null) {
            Skript.error("Invalid group name: " + group + ", refer to the wiki in order to create a slash command group.");
            return false;
        }

        parsedCommand.setGroup(parsedGroup);
        return true;
    }

    //endregion

    @Override
    public @NotNull Priority getPriority() {
        return PRIORITY;
    }
}
