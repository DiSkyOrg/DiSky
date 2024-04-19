package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RetrieveEmotes extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveEmotes.class,
                "retrieve [(all|every)] emotes (from|with|of|in) %guild% [(with|using) [the] [bot] %-bot%] and store (them|the emotes) in %~objects%"
        );
    }

    private Expression<Guild> exprGuild;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprGuild = (Expression<Guild>) expressions[0];
        exprBot = (Expression<Bot>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Emote[].class);
    }

    @Override
    protected void execute(Event event) {
        Guild guild = exprGuild.getSingle(event);
        Bot bot = exprBot == null ? Bot.any() : exprBot.getSingle(event);
        if (guild == null || bot == null)
            return;

        guild = bot.getInstance().getGuildById(guild.getId());
        if (guild == null)
            return;

        final List<RichCustomEmoji> emotes;
        try {
            emotes = guild.retrieveEmojis().complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        exprResult.change(event, emotes.stream().map(Emote::fromJDA).toArray(Emote[]::new), Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "retrieve emotes from guild " + exprGuild.toString(e, debug)
                + (exprBot != null ? " using bot " + exprBot.toString(e, debug) : "")
                + " and store them in " + exprResult.toString(e, debug);
    }
}
