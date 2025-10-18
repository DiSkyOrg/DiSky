package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.PinnedMessagePaginationAction;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RetrievePinnedMessages extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrievePinnedMessages.class,
                "retrieve [[last] %-number% [amount of]] pinned message[s] (of|in|from) %channel% and store (them|the messages) in %-objects%"
        );
    }

    private Expression<Number> exprAmount;
    private Expression<MessageChannel> exprChannel;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprAmount = (Expression<Number>) expressions[0];
        exprChannel = (Expression<MessageChannel>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, PinnedMessagePaginationAction.PinnedMessage[].class);
    }

    @Override
    public void execute(@NotNull Event e) {
        final Number number = EasyElement.parseSingle(exprAmount, e, -1);
        final MessageChannel channel = exprChannel.getSingle(e);
        if (number == null || channel == null)
            return;

        final List<PinnedMessagePaginationAction.PinnedMessage> messages;
        try {
            if (number.intValue() != -1)
                messages = channel.retrievePinnedMessages()
                        .takeAsync(number.intValue())
                        .get();
            else
                messages = channel.retrievePinnedMessages().complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex);
            return;
        }

        exprResult.change(e, messages.toArray(new PinnedMessagePaginationAction.PinnedMessage[0]), Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "retrieve last " + (exprAmount == null ? "50" : exprAmount.toString(e, debug)) + " pinned messages from " + exprChannel.toString(e, debug)
                + (exprResult != null ? " and store them in " + exprResult.toString(e, debug) : "");
    }
}
