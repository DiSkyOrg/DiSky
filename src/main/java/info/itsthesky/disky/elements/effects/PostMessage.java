package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import de.leonhard.storage.shaded.jetbrains.annotations.Nullable;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Post Message")
@Description({"Post a specific message as text, embed or message builder to a channel."})
@Examples({
        "# Send a message to a guild channel",
        "post \"Hello World\" to guild channel with id \"000\"",
        "# Send a message to a member",
        "open private channel of event-member and store it in {_channel}",
        "post \"Hello World\" to {_channel}",
})
public class PostMessage extends SpecificBotEffect<Message> {

    static {
        Skript.registerEffect(PostMessage.class,
                "(post|dispatch) [the] [message] %string% (in|to) [the] [channel] %channel%" +
                        " [and store (it|the message) (inside|in) %-objects%]");
    }

    private Expression<Object> exprMessage;
    private Expression<Object> exprReceiver;

    @Override
    public void runEffect(Event e, Bot bot) {
        final Object content = exprMessage.getSingle(e);
        final Object receiver = exprReceiver.getSingle(e);
        if (anyNull(content, receiver)) {
            restart();
            return;
        }
        // TODO: 30/01/2022 Allow the new type to be parsed (embed, message builder, etc...)
        final String message = content.toString();

        final MessageChannel channel = bot != null ?
                bot.findMessageChannel((MessageChannel) receiver) : (MessageChannel) receiver;

        event = e;
        channel
                .sendMessage(message)
                .queue(this::restart);
    }

    @Override
    public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMessage = (Expression<Object>) expressions[0];
        exprReceiver = (Expression<Object>) expressions[1];
        return expressions[2] == null || validateVariable(expressions[2], false);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "post the message " + exprMessage.toString(e, debug) + " in / to " + exprReceiver.toString(e, debug) +
                (changedVariable == null ? "" : " and store the message in " + changedVariable.toString(e, debug));
    }
}
