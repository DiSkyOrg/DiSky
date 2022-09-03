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
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.emojis.Emoji;
import info.itsthesky.disky.api.emojis.Emojis;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

        /* Trying to get it from the specified guild, if set */
        if (guild != null) {
            DiSky.debug("Trying to get emote from guild " + guild.getName() + " ...");
            final RichCustomEmoji richCustomEmoji = getFromGuild(guild, input);
            if (richCustomEmoji != null)
                return new Emote(richCustomEmoji);
        }

        /* Trying named emoji first */
        DiSky.debug("Trying to get named emoji " + input + " ...");
        Emoji namedEmoji = Emojis.ofShortcode(input);
        if (namedEmoji != null)
            return new Emote(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(namedEmoji.unicode()));

        /* Trying unicode through the internal API */
        DiSky.debug("Trying to get unicode emoji " + input + " from internal API ...");
        Emoji internalUnicodeEmoji = Emojis.ofUnicode(input);
        if (internalUnicodeEmoji != null)
            return new Emote(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(internalUnicodeEmoji.unicode()));

        DiSky.debug("Trying to get unicode emoji " + input + " from JDA's API ...");
        UnicodeEmoji unicodeEmoji = net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode(input);
        return new Emote(unicodeEmoji);
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