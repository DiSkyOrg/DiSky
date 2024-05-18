package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Retrieve Thread Members")
@Description({"Retrieve every members (and cache them) from a specific thread."})
public class RetrieveThreadMembers extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveThreadMembers.class,
                "retrieve [(all|every)] thread members (from|with|of|in) %threadchannel% [(with|using) [the] [bot] %-bot%] and store (them|the thread members) in %~objects%"
        );
    }

    private Expression<ThreadChannel> exprChannel;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprChannel = (Expression<ThreadChannel>) expressions[0];
        exprBot = (Expression<Bot>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Member[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        ThreadChannel channel = exprChannel.getSingle(event);
        Bot bot = Bot.fromContext(exprBot, event);
        if (channel == null || bot == null)
            return;

        channel = bot.getInstance().getThreadChannelById(channel.getId());
        if (channel == null)
            return;

        final List<ThreadMember> members;
        try {
            members = channel.retrieveThreadMembers().complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        exprResult.change(event, members.stream().map(ThreadMember::getMember).toArray(), Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "retrieve thread members from " + exprChannel.toString(event, b)
                + (exprBot == null ? "" : " using " + exprBot.toString(event, b))
                + " and store them in " + exprResult.toString(event, b);
    }
}
