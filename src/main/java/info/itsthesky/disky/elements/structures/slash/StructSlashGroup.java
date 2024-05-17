package info.itsthesky.disky.elements.structures.slash;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import info.itsthesky.disky.api.skript.entries.MutexEntryData;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.structures.slash.models.ParsedGroup;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StructSlashGroup extends Structure {

    public static final Priority PRIORITY = new Priority(750);
    private static final Pattern STRUCTURE = Pattern.compile("slash (command)? group ([A-z]+)");

    private static final EntryValidator CORE_VALIDATOR = EntryValidator.builder()

            .addEntryData(new MutexEntryData<>("description", "def", false,
                    SkriptUtils.custom(), String::valueOf))
            .addSection("name", true)

            .build();

    static {
        Skript.registerStructure(
                StructSlashGroup.class,
                "slash [command] group [named] %string%"
        );
    }

    private ParsedGroup parsedGroup;
    private EntryContainer entryContainer;
    private @NotNull Node structure;
    private Node node;

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult, @NotNull EntryContainer entryContainer) {
        this.entryContainer = entryContainer;
        this.structure = getEntryContainer().getSource();
        this.node = getParser().getNode();
        return true;
    }

    @Override
    public boolean load() {
        parsedGroup = new ParsedGroup();

        boolean name = parseGroupName();
        if (!name)
            return false;

        boolean description = parseDescription();
        if (!description)
            return false;

        SlashGroupManager.register(parsedGroup);

        return true;
    }

    //region Group Name

    private boolean parseGroupName() {
        final String rawStructure = entryContainer.getSource().getKey();
        final Matcher matcher = STRUCTURE.matcher(rawStructure.split("#")[0]);
        if (!matcher.matches()) {
            Skript.error("Invalid structure pattern: " + entryContainer.getSource().getKey());
            return false;
        }
        parsedGroup.setName(matcher.group(2));

        final @Nullable SectionNode localization = entryContainer.getOptional("name", SectionNode.class, true);
        if (localization == null)
            return true;
        final Map<DiscordLocale, String> localizations = StructSlashCommand.parseLocalizations(localization);
        if (localizations == null)
            return false;

        parsedGroup.setNameLocalizations(localizations);
        return true;
    }

    private boolean parseDescription() {
        final MutexEntryData.MutexEntry<String> description = entryContainer.get("description",
                MutexEntryData.MutexEntry.class, true);
        if (description.isComplex()) {
            final EntryContainer subs = description.getEntryContainer();
            if (subs == null)
                return false;

            final Map<DiscordLocale, String> localizations = StructSlashCommand.parseLocalizations(subs.getSource());
            if (localizations == null)
                return false;
            final String defaultDescription = localizations.get(DiscordLocale.ENGLISH_US);
            if (defaultDescription == null) {
                Skript.error("You must specify a default description for the command. (en-US)");
                return false;
            }

            parsedGroup.setDescriptionLocalizations(localizations);
            parsedGroup.setDescription(defaultDescription);
        } else {
            parsedGroup.setDescription(description.getValue());
        }

        return true;
    }

    //endregion

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "slash command group " + parsedGroup.getName();
    }

    @Override
    public @NotNull Priority getPriority() {
        return PRIORITY;
    }
}
