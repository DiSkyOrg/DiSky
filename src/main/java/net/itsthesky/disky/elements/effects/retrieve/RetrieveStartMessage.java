package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Retrieve Thread Start Message")
@Description({"Retrieve the starting message of a thread channel and store it in a variable."})
@Examples({"retrieve start message from event-threadchannel and store it in {_msg}"})
@Since("4.0.0")
public class RetrieveStartMessage extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveStartMessage.class,
                "retrieve start message (from|with|of|in) %threadchannel% and store (it|the message) in %~objects%"
        );
    }

    private Expression<ThreadChannel> exprChannel;
    private Expression<?> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprChannel = (Expression<ThreadChannel>) expressions[0];
        exprResult = expressions[1];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        ThreadChannel channel = exprChannel.getSingle(event);
        if (channel == null)
            return;

        final Message message;
        try {
            message = channel.retrieveStartMessage().complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        exprResult.change(event, new Message[] {message}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "retrieve start message from " + exprChannel.toString(event, debug) + " and store it in " + exprResult.toString(event, debug);
    }
}
