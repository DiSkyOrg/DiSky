package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Channel")
@Description({"Get a channel from a guild using its unique ID.",
        "Channels are global on discord, means different channels cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("guild channel with id \"000\"")
@Since("4.0.0")
@SeeAlso(GuildChannel.class)
public class GetGuildChannel extends BaseGetterExpression<GuildChannel> {

    static {
        register(GetGuildChannel.class,
                GuildChannel.class,
                "guild channel");
    }

    @Override
    protected GuildChannel get(String id, Bot bot) {
        return bot.getInstance().getGuildChannelById(id);
    }

    @Override
    public String getCodeName() {
        return "channel";
    }

    @Override
    public @NotNull Class<? extends GuildChannel> getReturnType() {
        return GuildChannel.class;
    }
}
