package info.itsthesky.disky.elements.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyPropertyCondition;
import info.itsthesky.disky.api.skript.PropertyCondition;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Member / Bot is in Thread")
@Description({"Check if a specific member or bot is in a guild thread.",
"Useful to avoid exception while using join & leave effects."})
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
