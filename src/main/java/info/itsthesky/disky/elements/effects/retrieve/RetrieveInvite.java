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

public class RetrieveInvite extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveInvite.class,
                "retrieve invite (with|from) id %string% (from|with|of|in) %guild% [(with|using) [the] [bot] %-bot%] and store (it|the invite) in %~objects%"
        );
    }

    private Expression<String> exprID;
    private Expression<Guild> exprGuild;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprID = (Expression<String>) expressions[0];
        exprGuild = (Expression<Guild>) expressions[1];
        exprBot = (Expression<Bot>) expressions[2];
        exprResult = (Expression<Object>) expressions[3];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Invite.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        String id = exprID.getSingle(event);
        Guild guild = exprGuild.getSingle(event);
        Bot bot = Bot.fromContext(exprBot, event);
        if (id == null || guild == null || bot == null)
            return;

        guild = bot.getInstance().getGuildById(guild.getId());
        if (guild == null)
            return;

        final Invite invite;
        try {
            invite = Invite.resolve(guild.getJDA(), id).complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        exprResult.change(event, new Invite[] {invite}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "retrieve invite with id " + exprID.toString(e, debug)
                + " from guild " + exprGuild.toString(e, debug)
                + (exprBot != null ? " using bot " + exprBot.toString(e, debug) : "")
                + " and store it in " + exprResult.toString(e, debug);
    }
}
