package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Member / Bot is in Thread")
@Description({
        "Check if a specific member or bot is in a guild thread.",
        "Useful to avoid exception while using join & leave effects."
})
@Examples({
        "if event-member is in event-threadchannel:",
        "if bot named \"MyBot\" is not in {_thread}:"
})
@Since("4.0.0")
@SeeAlso({Member.class, ThreadChannel.class})
public class BotMemberIsInThread extends Condition {

    static {
        Skript.registerCondition(BotMemberIsInThread.class,
                "%members/bots% (is|are) in [the] [thread] %threadchannel%",
                "%members/bots% (isn't|aren't|is not|are not) in [the] [thread] %threadchannel%"
        );
    }

    private Expression<?> exprEntities;
    private Expression<ThreadChannel> exprThread;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern,
                        @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprEntities = exprs[0];
        exprThread = (Expression<ThreadChannel>) exprs[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        final ThreadChannel thread = exprThread.getSingle(event);
        if (thread == null)
            return isNegated();

        return exprEntities.check(event, entity -> {
            final String id;
            if (entity instanceof Member member)
                id = member.getId();
            else if (entity instanceof Bot bot)
                id = bot.getInstance().getSelfUser().getId();
            else
                return false;

            return thread.getMembers().stream()
                    .anyMatch(m -> m.getId().equalsIgnoreCase(id));
        }, isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return exprEntities.toString(event, debug)
                + (isNegated() ? " is not" : " is")
                + " in thread " + exprThread.toString(event, debug);
    }
}