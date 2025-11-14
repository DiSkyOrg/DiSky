package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Open Private Channel")
@Description({"Opens a private channel with a specific user.",
        "The opened channel can be null and an exception can be thrown if the user does not accept messages."})
@Examples({"open private channel of event-user and store it in {_channel}",
        "if {_channel} is not set:",
        "\treply with \"Please enable your private messages!\""})
@Since("4.0.0")
@SeeAlso({User.class, PrivateChannel.class})
public class OpenPrivateChannel extends AsyncEffect {

    static {
        Skript.registerEffect(
                OpenPrivateChannel.class,
                "open [the] private (channel|message[s]) of [the] [member] %user% and store (it|the [private] channel) in %objects%"
        );
    }

    private Expression<User> exprUser;
    private Expression<Object> exprResult;
    private Node node;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);
        node = getParser().getNode();

        exprUser = (Expression<User>) exprs[0];
        exprResult = (Expression<Object>) exprs[1];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, PrivateChannel.class);
    }

    @Override
    public void execute(@NotNull Event e) {
        final User rawUser = parseSingle(exprUser, e, null);
        DiSky.debug("Opening private channel of user " + (rawUser == null ? "[user not set]" : rawUser.getId()) + " ...");
        if (rawUser == null) {
            DiSkyRuntimeHandler.exprNotSet(node, exprUser);
            return;
        }

        try {

            final PrivateChannel channel = rawUser.openPrivateChannel().complete();
            if (channel == null) {
                DiSkyRuntimeHandler.error(new IllegalStateException("The private channel of user " + rawUser.getId() + " is null!"), node);
                exprResult.change(e, new Object[0], Changer.ChangeMode.SET);
                return;
            }

            exprResult.change(e, new Object[] {channel}, Changer.ChangeMode.SET);
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, node);
            exprResult.change(e, new Object[0], Changer.ChangeMode.SET);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "open private message of " + exprUser.toString(e, debug)
                + " and store it in " + exprResult.toString(e, debug);
    }
}
