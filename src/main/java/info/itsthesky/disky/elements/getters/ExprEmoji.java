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
import info.itsthesky.disky.api.emojis.Emojis;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.managers.BotManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
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
        return convert(guild, emotes).toArray(new Emote[0]);
    }

    public List<Emote> convert(@Nullable Guild guild, String... emotes) {
        List<Emote> emojis = new ArrayList<>();
        for (String input : emotes) {

            Emote emote;
            try {
                emote = new Emote(input.toLowerCase(Locale.ROOT), Emojis.ofShortcode(input.toLowerCase(Locale.ROOT)).unicode());
            } catch (NullPointerException ex) {
                final boolean useID = input.matches("[^0-9]");
                if (guild == null) {

                    emote = DiSky
                            .getManager()
                            .getBots()
                            .stream()
                            .map(Bot::getInstance)
                            .map(JDA::getGuilds)
                            .map(guilds -> {
                                for (Guild guild1 : guilds) {
                                    if (getFromGuild(input, guild1, useID) == null)
                                        continue;
                                    return getFromGuild(input, guild1, useID);
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);

                } else {

                    emote = getFromGuild(input, guild, useID);

                }
            }

            if (emote == null)
            {
                Skript.warning("Cannot found the emote named " + input);
                continue;
            }
            emojis.add(emote);
        }
        return emojis;
    }

    public Emote getFromGuild(String input, Guild guild, boolean useID) {
        return Emote.fromJDA(guild
                .getEmotes()
                .stream()
                .filter(e -> useID ? e.getId().equalsIgnoreCase(input) : e.getName().equalsIgnoreCase(input))
                .findAny()
                .orElse(null));
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