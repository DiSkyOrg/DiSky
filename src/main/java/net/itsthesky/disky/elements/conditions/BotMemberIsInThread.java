package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyPropertyCondition;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Member / Bot is in Thread")
@Description({"Check if a specific member or bot is in a guild thread.",
"Useful to avoid exception while using join & leave effects."})
@Examples({"if event-member is in event-threadchannel:",
        "if bot named \"MyBot\" is not in {_thread}:"})
@Since("4.0.0")
@SeeAlso({Member.class, ThreadChannel.class})
public class BotMemberIsInThread extends EasyPropertyCondition<Object> {

    static {
        register(
                BotMemberIsInThread.class,
                PropertyCondition.PropertyType.BE,
                "in [the] thread %threadchannel%",
                "member/bot"
        );
    }

    private Expression<ThreadChannel> exprThread;

    @Override
    public boolean check(Event e, Object entity) {
        final String id = entity instanceof Member ? ((Member) entity).getId() : (((Bot) entity).getInstance().getSelfUser()).getId();
        final ThreadChannel thread = exprThread.getSingle(e);
        if (id == null || thread == null)
            return false;
        final boolean contains = thread
                .getMembers()
                .stream()
                .filter(member -> member.getId().equalsIgnoreCase(id))
                .findAny()
                .orElse(null) != null;
        return isNegated() != contains;
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprThread = (Expression<ThreadChannel>) exprs[0];
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }
}
