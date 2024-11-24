package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static info.itsthesky.disky.api.skript.EasyElement.anyNull;
import static info.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Open Private Channel")
@Description({"Opens a private channel with a specific user.",
        "The channel will be null if the id is not valid or if the target is the bot",
             "WARNING: THIS DOESNT RETURN NULL WHEN THE USER BLOCKED THE BOT OR IS NOT ABLE TO SEND THE USER A MESSAGE"})
@Examples("open private channel of event-user and store it in {_channel}")
public class OpenPrivateChannel extends AsyncEffect {

    static {
        Skript.registerEffect(
                OpenPrivateChannel.class,
                "open [the] private (channel|message[s]) of [the] [member] %user% and store (it|the [private] channel) in %objects%"
        );
    }

    private Expression<User> exprUser;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprUser = (Expression<User>) exprs[0];
        exprResult = (Expression<Object>) exprs[1];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, PrivateChannel.class);
    }

    @Override
    public void execute(@NotNull Event e) {
        final User rawUser = parseSingle(exprUser, e, null);
        DiSky.debug("Opening private channel of user " + rawUser.getId() + " ...");
        if (anyNull(this, rawUser))
            return;

        try {

            final PrivateChannel channel = rawUser.openPrivateChannel().complete();
            if (channel == null) {
                DiSky.debug("The private channel of user " + rawUser.getId() + " is null.");
                exprResult.change(e, new Object[0], Changer.ChangeMode.SET);
                return;
            }

            DiSky.debug("The private channel of user " + rawUser.getId() + " is not null. Storing ...");
            exprResult.change(e, new Object[] {channel}, Changer.ChangeMode.SET);
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(e, ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "open private message of " + exprUser.toString(e, debug)
                + " and store it in " + exprResult.toString(e, debug);
    }
}
