package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.anyNull;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Create Thread")
@Description({"Create a new thread in a text channel with a base name.",
        "The bot used in that effect will automatically join the thread, so you don't have to make it join yourself.",
        "If you create a private thread, then you cannot specify a message.",
        "Else, the Thread will be created based on the specified message.",
        "Creating private thread need the guild to be level 2 or more, else it'll throw an exception."})
@Examples({"create thread named \"Discussion\" in event-channel and store it in {_thread}",
        "create private thread named \"Staff Room\" in channel with id \"000\""})
@Since("4.0.0")
@SeeAlso({Message.class, ThreadChannel.class})
public class CreateThread extends AsyncEffect {

    static {
        Skript.registerEffect(
                CreateThread.class,
                "(make|create) [the] [new] [1:private] thread (named|with name) %string% in [the] [channel] %channel/textchannel% [(with|using) [the] [message] [as reference] %-message%] [(with|using) [the] [bot] %-bot%] [and store (it|the thread) in %-~objects%]"
        );
    }

    private Expression<String> exprName;
    private Expression<Message> exprMessage;
    private Expression<StandardGuildMessageChannel> exprChannel;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;
    private boolean isPrivate = false;

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create new role named " + exprName.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprName = (Expression<String>) exprs[0];
        exprChannel = (Expression<StandardGuildMessageChannel>) exprs[1];
        exprMessage = (Expression<Message>) exprs[2];
        exprBot = (Expression<Bot>) exprs[3];
        exprResult = (Expression<Object>) exprs[4];
        isPrivate = parseResult.hasTag("1");
        return exprResult == null || Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, ThreadChannel.class);
    }

    @Override
    public void execute(@NotNull Event e) {
        final String name = exprName.getSingle(e);
        final @Nullable Message message = parseSingle(exprMessage, e, null);
        final Bot bot =  Bot.fromContext(exprBot, e);
        StandardGuildMessageChannel channel = exprChannel.getSingle(e);
        if (anyNull(this, bot, name, channel))
            return;

        channel = bot.getInstance().getChannelById(StandardGuildMessageChannel.class, channel.getId());
        if (channel == null)
            return;

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

        final ThreadChannel threadChannel;
        try {
            threadChannel = action.complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        if (exprResult != null)
            exprResult.change(e, new ThreadChannel[] {threadChannel}, Changer.ChangeMode.SET);
    }
}
