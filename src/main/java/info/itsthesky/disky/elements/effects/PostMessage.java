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
import info.itsthesky.disky.core.JDAUtils;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                "(post|dispatch) [the] [message] %string/embedbuilder/messagebuilder% (in|to) [the] [channel] %channel%" +
                        " [with [the] (component|action)[s] [row] %-rows%] [and store (it|the message) (inside|in) %-objects%]");
    }

    private Expression<Object> exprMessage;
    private Expression<Object> exprReceiver;
    private Expression<ComponentRow> exprComponents;

    @Override
    public void runEffect(Event e, Bot bot) {
        final Object rawContent = exprMessage.getSingle(e);
        final MessageBuilder content = JDAUtils.constructMessage(rawContent);
        final Object receiver = exprReceiver.getSingle(e);
        final List<ComponentRow> rows = Arrays.asList(parseList(exprComponents, e, new ComponentRow[0]));
        if (anyNull(content, receiver)) {
            restart();
            return;
        }

        final List<ActionRow> formatted = rows
                .stream()
                .map(ComponentRow::asActionRow)
                .collect(Collectors.toList());

        final MessageChannel channel = bot != null ?
                bot.findMessageChannel((MessageChannel) receiver) : (MessageChannel) receiver;

        event = e;
        channel.sendMessage(content.build())
                .setActionRows(formatted)
                .queue(this::restart);
    }

    @Override
    public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMessage = (Expression<Object>) expressions[0];
        exprReceiver = (Expression<Object>) expressions[1];
        exprComponents = (Expression<ComponentRow>) expressions[2];
        return expressions[3] == null || validateVariable(expressions[3], false);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "post the message " + exprMessage.toString(e, debug) + " in / to " + exprReceiver.toString(e, debug) +
                (changedVariable == null ? "" : " and store the message in " + changedVariable.toString(e, debug));
    }
}
