package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenPrivateChannel extends SpecificBotEffect<PrivateChannel> {

    static {
        Skript.registerEffect(
                OpenPrivateChannel.class,
                "open [the] private (channel|message[s]) of [the] [member] %user% and store (it|the [private] channel) in %objects%"
        );
    }

    private Expression<User> exprUser;

    @Override
    public boolean initEffect(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprUser = (Expression<User>) exprs[0];
        return validateVariable(exprs[1], false);
    }

    @Override
    public void runEffect(Event e, Bot bot) {
        final User rawUser = parseSingle(exprUser, e, null);
        if (anyNull(rawUser, bot)) {
            restart();
            return;
        }

        if (bot.getInstance().getUserById(rawUser.getId()) == null) {
            bot
                    .getInstance()
                    .retrieveUserById(rawUser.getId())
                    .queue(user -> {
                        Utils.catchAction(user.openPrivateChannel(),
                                this::restart,
                                ex -> {
                                    DiSky.getErrorHandler().exception(e, ex);
                                    restart();
                                });
                    });
        } else {
            Utils.catchAction(rawUser.openPrivateChannel(),
                    this::restart,
                    ex -> {
                        DiSky.getErrorHandler().exception(e, ex);
                        restart();
                    });
        }

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "open private message of " + exprUser.toString(e, debug) + " and store it in " + changedVariable.toString(e, debug);
    }
}
