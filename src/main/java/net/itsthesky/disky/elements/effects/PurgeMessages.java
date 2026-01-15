package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.AsyncEffect;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.itsthesky.disky.api.skript.EasyElement.parseList;

@Name("Purge Messages")
@Description({"Discord provide a better way to delete multiple messages at once.",
"This effect only works with messages, and a list is recommended here.",
"If you want to delete a single message, use the destroy effect."})
@Examples({"retrieve last 50 messages from event-channel and store them in {_msg::*}",
"purge {_msg::*}"})
@Since("4.0.0")
@SeeAlso({Message.class, MessageChannel.class})
public class PurgeMessages extends AsyncEffect {

    static {
        Skript.registerEffect(
                PurgeMessages.class,
                "purge [the] [message[s]] %messages%"
        );
    }

    private Expression<Message> exprMessages;

    @Override
    public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprMessages = (Expression<Message>) expressions[0];
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Message[] messages = parseList(exprMessages, e, new Message[0]);
        if (messages.length == 0)
            return;

        final HashMap<MessageChannel, List<Message>> sorted = new HashMap<>();
        for (Message message : messages) {
            if (!sorted.containsKey(message.getChannel()))
                sorted.put(message.getChannel(), new ArrayList<>());
            sorted.get(message.getChannel()).add(message);
        }

        for (MessageChannel channel : sorted.keySet()) {
            List<Message> toDelete = sorted.get(channel);
            if (toDelete.isEmpty())
                continue;

            if (toDelete.size() == 1) {
                toDelete.get(0).delete().queue();
            } else {
                channel.purgeMessages(toDelete).forEach(CompletableFuture::join);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "purge " + exprMessages.toString(e, debug);
    }
}
