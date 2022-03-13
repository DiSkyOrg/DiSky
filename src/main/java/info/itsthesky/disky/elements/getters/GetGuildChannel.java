package info.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.GuildChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Channel")
@Description({"Get a channel from a guild using its unique ID.",
        "Channels are global on discord, means different channels cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("channel with id \"000\"")
public class GetGuildChannel extends BaseGetterExpression<GuildChannel> {

    static {
        register(GetGuildChannel.class,
                GuildChannel.class,
                "channel");
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
