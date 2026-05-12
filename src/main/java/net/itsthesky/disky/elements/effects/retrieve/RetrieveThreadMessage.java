package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Retrieve Thread Message")
@Description({"Retrieve the start message or parent (original) message of a thread channel.",
        "Use `retrieve start message` to get the message that opened the thread, or `retrieve original message` to get the parent channel's message."})
@Examples({"retrieve start message from event-threadchannel and store it in {_msg}",
        "retrieve original message from event-threadchannel and store it in {_parent}"})
@Since("4.0.0")
public class RetrieveThreadMessage extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveThreadMessage.class,
                "retrieve (:start|(original|parent)) message (from|of) %threadchannel% and store (it|the message) in %~object%"
        );
    }

    private Expression<ThreadChannel> exprThread;
    private Expression<Object> exprResult;
    private boolean start = false;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        exprThread = (Expression<ThreadChannel>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];
        start = parseResult.hasTag("start");
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        ThreadChannel thread = exprThread.getSingle(event);
        if (thread == null)
            return;

        final Message message;
        try {
            message = (start ?
                    thread.retrieveStartMessage() :
                    thread.retrieveParentMessage()
            ).complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        exprResult.change(event, new Message[] {message}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "retrieve " + (start ? "start" : "parent") + " message of " + exprThread.toString(event, debug) + " and store it in " + exprResult.toString(event, debug);
    }
}
