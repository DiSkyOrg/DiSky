package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndPoll extends AsyncEffect {

    static {
        Skript.registerEffect(EndPoll.class,
                "end [the] poll of [the] [message] %message% [now]"
        );
    }

    private Node node;
    private Expression<Message> exprMessage;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        node = getParser().getNode();

        exprMessage = (Expression<Message>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        final Message message = exprMessage.getSingle(event);
        if (message == null)
            return;

        if (message.getPoll() == null) {
            SkriptUtils.error(node, "The provided message is not a poll message!");
            return;
        }

        if (message.getPoll().isExpired()) {
            SkriptUtils.error(node, "The provided poll is already expired!");
            return;
        }

        if (message.getPoll().isFinalizedVotes()) {
            SkriptUtils.error(node, "The provided poll is already finalized!");
            return;
        }

        try {
            message.endPoll().complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "end the poll of message " + exprMessage.toString(event, debug);
    }

}
