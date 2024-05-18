package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RetrieveMessage extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveMessage.class,
                "retrieve message (with|from) id %string% (from|with|of|in) %channel% [(with|using) [the] [bot] %-bot%] and store (it|the message) in %~objects%"
        );
    }

    private Expression<String> exprID;
    private Expression<Channel> exprChannel;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;
    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprID = (Expression<String>) expressions[0];
        exprChannel = (Expression<Channel>) expressions[1];
        exprBot = (Expression<Bot>) expressions[2];
        exprResult = (Expression<Object>) expressions[3];
        node = getParser().getNode();

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        String id = exprID.getSingle(event);
        Bot bot = Bot.fromContext(exprBot, event);
        Channel rawChannel = exprChannel.getSingle(event);
        if (id == null || bot == null || rawChannel == null)
            return;

        if (!(rawChannel instanceof MessageChannel)) {
            SkriptUtils.error(node, "The channel must be a message channel!");
            return;
        }
        MessageChannel channel = (MessageChannel) rawChannel;

        channel = bot.getInstance().getChannelById(MessageChannel.class, channel.getId());
        if (channel == null)
            return;

        final Message message;
        try {
            message = channel.retrieveMessageById(id).complete();
        } catch (Exception ex) {
            return;
        }

        exprResult.change(event, new Message[] {message}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "retrieve message with id " + exprID.toString(event, b)
                + " from channel " + exprChannel.toString(event, b)
                + (exprBot == null ? "" : " using bot " + exprBot.toString(event, b))
                + " and store it in " + exprResult.toString(event, b);
    }
}
