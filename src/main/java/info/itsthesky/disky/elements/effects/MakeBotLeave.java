package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class MakeBotLeave extends AsyncEffect {

    static {
        Skript.registerEffect(
                MakeBotLeave.class,
                "make [the] %bot% leave [the] [guild] %guild%"
        );
    }

    private Expression<Bot> exprBot;
    private Expression<Guild> exprGuild;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprBot = (Expression<Bot>) expressions[0];
        exprGuild = (Expression<Guild>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        final Bot bot = Bot.fromContext(exprBot, event);
        final Guild guild = exprGuild.getSingle(event);
        if (guild == null)
            return;

        try {
            final Guild botGuild = bot.getInstance().getGuildById(guild.getId());
            if (botGuild == null)
                return;
            botGuild.leave().complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "make bot " + exprBot.toString(event, debug) + " leave guild " + exprGuild.toString(event, debug);
    }
}
