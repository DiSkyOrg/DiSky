package info.itsthesky.disky.elements.effects.retrieve;

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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Retrieve Messages")
@Description({"Retrieve last X messages from a specific message channel.",
"You can retrieve up to 100 last messages, others will be ignored.",
"Don't forget to use 'purge' effect to delete a lot of messages the most enhanced way ever."})
@Examples("retrieve last 30 messages from event-channel and store them in {_msg::*}")
public class RetrieveMessages extends SpecificBotEffect<List<Message>> {

    static {
        Skript.registerEffect(
                RetrieveMessages.class,
                "retrieve [last] %number% [amount of] message[s] (of|in|from) %channel% and store (them|the messages) in %-objects%"
        );
    }

    private Expression<Number> exprAmount;
    private Expression<MessageChannel> exprChannel;

    @Override
    public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprAmount = (Expression<Number>) expressions[0];
        exprChannel = (Expression<MessageChannel>) expressions[1];
        return validateVariable(expressions[2], true);
    }

    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        final Number number = parseSingle(exprAmount, e, null);
        final MessageChannel channel = parseSingle(exprChannel, e, null);
        if (anyNull(number, channel)) {
            restart();
            return;
        }

        channel.getHistory().retrievePast(number.intValue()).queue(this::restart, ex -> {
            DiSky.getErrorHandler().exception(e, ex);
            restart();
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "retrieve last " + exprAmount.toString(e, debug) + " from " + exprChannel.toString(e, debug);
    }
}
