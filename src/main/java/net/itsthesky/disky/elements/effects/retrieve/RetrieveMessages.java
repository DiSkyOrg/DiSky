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
import net.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Retrieve Messages")
@Description({"Retrieve last X messages from a specific message channel.",
"You can retrieve up to 100 last messages, others will be ignored.",
"Don't forget to use 'purge' effect to delete a lot of messages the most enhanced way ever."})
@Examples("retrieve last 30 messages from event-channel and store them in {_msg::*}")
public class RetrieveMessages extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveMessages.class,
                "retrieve [last] %number% [amount of] message[s] (of|in|from) %channel% and store (them|the messages) in %-objects%"
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
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message[].class);
    }

    @Override
    public void execute(@NotNull Event e) {
        final Number number = exprAmount.getSingle(e);
        final MessageChannel channel = exprChannel.getSingle(e);
        if (number == null || channel == null)
            return;

        final List<Message> messages;
        try {
            messages = channel.getHistory().retrievePast(number.intValue()).complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(e, ex);
            return;
        }

        exprResult.change(e, messages.toArray(new Message[0]), Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "retrieve last " + exprAmount.toString(e, debug) + " from " + exprChannel.toString(e, debug);
    }
}
