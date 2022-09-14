package info.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.emojis.Emoji;
import info.itsthesky.disky.api.emojis.Emojis;
import info.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Name("Emoji / Emote")
@Description({
        "Get an emoji or an emote from its name, ID or unicode.",
        "- An emoji is discord-side only, can be used everywhere, and don't have any attached guild.",
        "- An emote is guild-side only, have a custom long ID and are attached to a guild.",
        "It the specified reaction doesn't exist, DiSky will simply return null and say it in console.",
        "We highly recommend the specification of the guild when retrieving an emote, to avoid conflicts with other that potentially have the same name."
})
@Examples({
        "reaction \"joy\"",
        "emoji \"sparkles\"",
        "emote \"disky\" in event-guild",
})
public class ExprEmoji extends SimpleExpression<Emote> {
    static {
        Skript.registerExpression(ExprEmoji.class, Emote.class, ExpressionType.SIMPLE,
                "(emoji|emote|reaction)[s] %strings% [(from|in) %-guild%]");
    }

    private static final Pattern COMPLEX_CUSTOM = Pattern.compile("^(?:<a?:[a-zA-Z0-9_]+:)?([0-9]+)>?$");
    private static final Pattern SIMPLE_CUSTOM = Pattern.compile("^([0-9]+)$");
    private static final Pattern NAMED = Pattern.compile("^:([a-zA-Z0-9_]+):$");

    private Expression<String> name;
    private Expression<Guild> guild;

    @Override
    protected Emote @NotNull [] get(@NotNull Event e) {
        String[] emotes = name.getAll(e);
        Guild guild = this.guild == null ? null : this.guild.getSingle(e);
        if (emotes.length == 0) return new Emote[0];

        final List<Emote> parsed = new ArrayList<>();
        for (String input : emotes)
            parsed.add(parse(guild, input));

        return parsed.toArray(new Emote[0]);
    }

    public Emote parse(@Nullable Guild guild, String input) {

        final Matcher complexCustom = COMPLEX_CUSTOM.matcher(input);
        if (complexCustom.matches()) {
            final String id = complexCustom.group(1);
            if (guild == null) {
                Skript.warning("You must specify a guild when retrieving an emote by its ID!");
                return null;
            }
            final RichCustomEmoji emote = guild.getEmojiById(id);
            if (emote == null) {
                Skript.warning("The emote with ID '" + id + "' doesn't exist in the guild '" + guild.getName() + "'!");
                return null;
            }
            return new Emote(emote);
        }

        final Matcher simpleCustom = SIMPLE_CUSTOM.matcher(input);
        if (simpleCustom.matches()) {
            final String id = simpleCustom.group(1);
            if (guild == null) {
                Skript.warning("You must specify a guild when retrieving an emote by its ID!");
                return null;
            }
            final RichCustomEmoji emote = guild.getEmojiById(id);
            if (emote == null) {
                Skript.warning("The emote with ID '" + id + "' doesn't exist in the guild '" + guild.getName() + "'!");
                return null;
            }
            return new Emote(emote);
        }

        final String name;

        final Matcher named = NAMED.matcher(input);
        if (named.matches())
            name = named.group(1);
        else
            name = input;

        if (guild == null) {
            final Emoji emoji = Emojis.ofShortcode(name);
            if (emoji == null) {
                final Emoji unicodeEmoji = Emojis.ofUnicode(name);
                if (unicodeEmoji == null) {
                    Skript.warning("The emoji '" + name + "' doesn't exist!");
                    return null;
                }
                return new Emote(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(unicodeEmoji.unicode()));
            }
            return new Emote(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(emoji.unicode()));
        } else {
            final RichCustomEmoji emote = guild.getEmojisByName(name, true).stream().findFirst().orElse(null);
            if (emote == null) {
                Skript.warning("The emote with name '" + name + "' doesn't exist in the guild '" + guild.getName() + "'!");
                return null;
            }
            return new Emote(emote);
        }

    }

    public @Nullable RichCustomEmoji getFromGuild(Guild guild, String input) {
        for (RichCustomEmoji richCustomEmoji : guild.getEmojis())
            if (richCustomEmoji.getName().equalsIgnoreCase(input))
                return richCustomEmoji;

        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Emote> getReturnType() {
        return Emote.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "emoji named " + name.toString(e, debug) + (guild == null ? "" : " from " + guild.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        name = (Expression<String>) exprs[0];
        guild = (Expression<Guild>) exprs[1];
        return true;
    }
}