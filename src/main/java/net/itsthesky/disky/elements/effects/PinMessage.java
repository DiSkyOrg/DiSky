package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Pin / Unpin Message")
@Description("Pin or unpin a message in a channel. Each channel can have up to 50 pinned messages.")
@Examples({"pin event-message",
        "unpin last message in event-channel"})
@Since("4.0.0")
@SeeAlso(Message.class)
public class PinMessage extends AsyncEffect {

    static {
        Skript.registerEffect(
                PinMessage.class,
                "[:un]pin [the] [message] %message%"
        );
    }

    private Node node;
    private boolean unpin;
    private Expression<Message> exprMessage;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        node = getParser().getNode();
        exprMessage = (Expression<Message>) expressions[0];
        unpin = parseResult.hasTag("un");

        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        Message message = exprMessage.getSingle(event);
        if (message == null)
            return;

        final RestAction action;
        if (unpin) {
            if (!message.isPinned()) {
                SkriptUtils.error(node, "Cannot unpin a message which is not pinned!");
                return;
            }

            action = message.unpin();
        } else {
            if (message.isPinned()) {
                SkriptUtils.error(node, "Cannot pin a message which is already pinned!");
                return;
            }

            action = message.pin();
        }

        try {
            action.complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return (unpin ? "un" : "") + "pin message " + exprMessage.toString(event, debug);
    }
}
