package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Thread Channel")
@Description({"Get a thread channel from a guild using its unique ID.",
        "Threads are global on discord, means different threads cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("thread with id \"000\"")
public class GetThread extends BaseGetterExpression<ThreadChannel> {

    static {
        register(GetThread.class,
                ThreadChannel.class,
                "thread [channel]");
    }

    @Override
    protected ThreadChannel get(String id, Bot bot) {
        return bot.getInstance().getThreadChannelById(id);
    }

    @Override
    public String getCodeName() {
        return "thread";
    }

    @Override
    public @NotNull Class<? extends ThreadChannel> getReturnType() {
        return ThreadChannel.class;
    }
}
