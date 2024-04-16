package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class RetrieveInvites extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveInvites.class,
                "retrieve [the] invite[s] (from|with|of|in) %guild% [(with|using) [the] [bot] %-bot%] and store (it|the invites) in %~objects%"
        );
    }

    private Expression<Guild> exprGuild;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprGuild = (Expression<Guild>) expressions[0];
        exprBot = (Expression<Bot>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Invite[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        Guild guild = exprGuild.getSingle(event);
        Bot bot = exprBot == null ? Bot.any() : exprBot.getSingle(event);
        if (guild == null || bot == null)
            return;

        guild = bot.getInstance().getGuildById(guild.getId());
        if (guild == null)
            return;

        final Invite[] invites;
        try {
            invites = guild.retrieveInvites().complete().toArray(new Invite[0]);
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        exprResult.change(event, invites, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "retrieve invites from guild " + exprGuild.toString(e, debug)
                + (exprBot != null ? " using bot " + exprBot.toString(e, debug) : "")
                + " and store it in " + exprResult.toString(e, debug);
    }
}
