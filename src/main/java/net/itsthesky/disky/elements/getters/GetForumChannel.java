package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Forum Channel")
@Description({"Get a forum channel from a guild using its unique ID.",
        "Channels are global on discord, means different forum channels cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("forum channel with id \"000\"")
@Since("4.4.4")
public class GetForumChannel extends BaseGetterExpression<ForumChannel> {

    static {
        register(GetForumChannel.class,
                ForumChannel.class,
                "forum channel");
    }

    @Override
    protected ForumChannel get(String id, Bot bot) {
        return bot.getInstance().getForumChannelById(id);
    }

    @Override
    public String getCodeName() {
        return "forum channel";
    }

    @Override
    public @NotNull Class<? extends ForumChannel> getReturnType() {
        return ForumChannel.class;
    }
}
