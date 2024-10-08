package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.INodeHolder;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForwardMessage extends AsyncEffect implements INodeHolder {

    static {
        Skript.registerEffect(
            ForwardMessage.class,
                "forward [the] [message] %message% to %channel/textchannel%"
        );
    }

    private Expression<Message> exprMessage;
    private Expression<Object> exprChannel;
    private Node node;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprMessage = (Expression<Message>) expressions[0];
        exprChannel = (Expression<Object>) expressions[1];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        final var message = exprMessage.getSingle(event);
        final var rawChannel = exprChannel.getSingle(event);
        if (message == null || rawChannel == null)
            return;

        if (rawChannel instanceof final MessageChannel channel) {
            try {
                message.forwardTo(channel).complete();
            } catch (Exception ex) {
                DiSkyRuntimeHandler.error(ex, node);
            }
        } else {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The channel to forward the message to is not a (message) channel!"), node);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "forward message " + exprMessage.toString(event, debug) + " to " + exprChannel.toString(event, debug);
    }

    @Override
    @NotNull
    public Node getNode() {
        return node;
    }
}