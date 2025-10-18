package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Retrieve Threads")
@Description({"Retrieve every threads (and cache them) from a specific guild.",
        "This effect will only get back the ACTIVE thread, and will pass on the archived ones."})
public class RetrieveThreads extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveThreads.class,
                "retrieve [(all|every)] thread[s] (from|with|of|in) %guild/channel% [(with|using) [the] [bot] %-bot%] and store (them|the thread[s]) in %~objects%"
        );
    }

    private Expression<Object> exprGuild;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprGuild = (Expression<Object>) expressions[0];
        exprBot = (Expression<Bot>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, ThreadChannel[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        Object entity = exprGuild.getSingle(event);
        Bot bot = Bot.fromContext(exprBot, event);
        if (bot == null || !(entity instanceof IPostContainer))
            return;

        final List<ThreadChannel> threads;
        try {
            if (entity instanceof Guild)
                threads = ((Guild) entity).retrieveActiveThreads().complete();
            else if (entity instanceof ForumChannel)
                threads = ((ForumChannel) entity).retrieveArchivedPublicThreadChannels().complete();
            else
                threads = new ArrayList<>();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        exprResult.change(event, threads.toArray(new ThreadChannel[0]), Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "retrieve threads from guild " + exprGuild.toString(event, b)
                + (exprBot == null ? "" : " using bot " + exprBot.toString(event, b))
                + " and store them in " + exprResult.toString(event, b);
    }
}
