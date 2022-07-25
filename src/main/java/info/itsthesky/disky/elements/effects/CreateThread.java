package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.api.skript.WaiterEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Create Thread")
@Description({"Create a new thread in a text channel with a base name.",
        "The bot used in that effect will automatically join the thread, so you don't have to make it join yourself.",
        "If you create a private thread, then you cannot specify a message.",
        "Else, the Thread will be created based on the specified message.",
        "Creating private thread need the guild to be level 2 or more, else it'll throw an exception."})
public class CreateThread extends SpecificBotEffect<ThreadChannel> {

    static {
        Skript.registerEffect(
                CreateThread.class,
                "(make|create) [the] [new] [private] thread (named|with name) %string% in [the] [channel] %channel/textchannel% [(with|using) [the] [message] [as reference] %-message%] [(with|using) [the] [bot] %-bot%] and store (it|the thread) in %object%"
        );
    }

    private Expression<String> exprName;
    private Expression<Message> exprMessage;
    private Expression<BaseGuildMessageChannel> exprChannel;
    private boolean isPrivate = false;

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create new role named " + exprName.toString(e, debug);
    }

    @Override
    public boolean initEffect(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprName = (Expression<String>) exprs[0];
        exprChannel = (Expression<BaseGuildMessageChannel>) exprs[1];
        exprMessage = (Expression<Message>) exprs[2];
        setChangedVariable((Variable<ThreadChannel>) exprs[4]);
        isPrivate = parseResult.expr.contains("private thread");
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        final String name = exprName.getSingle(e);
        final @Nullable Message message = parseSingle(exprMessage, e, null);
        BaseGuildMessageChannel channel = exprChannel.getSingle(e);
        if (name == null || bot == null || channel == null) {
            restart();
            return;
        }
        channel = bot.getInstance().getTextChannelById(channel.getId());
        if (channel == null) {
            restart();
            return;
        }

        final RestAction<ThreadChannel> action;
        if (isPrivate) {
            action = channel.createThreadChannel(name, isPrivate);
        } else {
            if (message == null) {
                action = channel.createThreadChannel(name);
            } else {
                action = channel.createThreadChannel(name, message.getId());
            }
        }
        action.queue(this::restart);
    }
}
