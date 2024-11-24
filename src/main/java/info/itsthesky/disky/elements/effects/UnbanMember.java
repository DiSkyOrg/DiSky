package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Unban User")
@Description({"Unbans a user from a guild."})
@Examples({"unban event-user in guild with id \"818182471140114432\""})

public class UnbanMember extends SpecificBotEffect {

    static {
        Skript.registerEffect(
                UnbanMember.class,
                "[discord] un[-| ]ban [the] [discord] [user] %user% (from|in) [guild] %guild%"
        );
    }

    private Expression<User> exprUser;
    private Expression<Guild> exprGuild;

    @Override
    public boolean initEffect(Expression[] expr, int i, Kleenean kleenean, ParseResult parseResult) {
        exprUser = (Expression<User>) expr[0];
        exprGuild = (Expression<Guild>) expr[1];
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        final User user = parseSingle(exprUser, e, null);
        final Guild guild = parseSingle(exprGuild, e, null);

        if (user == null || guild == null || bot == null) {
            restart();
            return;
        }

        guild.unban(user).queue(
                s -> restart(),
                ex -> {
                    DiSky.getErrorHandler().exception(e, ex);
                    restart();
                }
        );

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "unbanned " + exprUser.toString(e, debug) + " from guild " + exprGuild.toString(e, debug);
    }
}