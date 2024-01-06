package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Open Private Channel")
@Description({"Opens a private channel with a specific user.",
        "The channel will be null if the id is not valid or if the target is the bot",
             "WARNING: THIS DOESNT RETURN NULL WHEN THE USER BLOCKED THE BOT OR IS NOT ABLE TO SEND THE USER A MESSAGE"})
@Examples("open private channel of event-user and store it in {_channel}")
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
    public void runEffect(@NotNull Event e, Bot bot) {
        final User rawUser = parseSingle(exprUser, e, null);
        DiSky.debug("Opening private channel of user " + rawUser.getId() + " ...");
        if (anyNull(this, rawUser, bot)) {
            restart();
            return;
        }

        final CacheRestAction<PrivateChannel> response = rawUser.openPrivateChannel();
        DiSky.debug("Request for private channel of user " + rawUser.getId() + " created. sending ...");

        response.queue(entity -> {
            restart(entity);
            DiSky.debug("Private channel of user " + rawUser.getId() + " opened and stored in " + changedVariable.toString(e, true) + ".");
        }, ex -> {
            DiSky.debug("Cannot open private channel of user " + rawUser.getId() + ": " + ex.getMessage() + ".");
            DiSky.getErrorHandler().exception(e, ex);
            restart();
        });

    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "open private message of " + exprUser.toString(e, debug) + " and store it in " + changedVariable.toString(e, debug);
    }
}
