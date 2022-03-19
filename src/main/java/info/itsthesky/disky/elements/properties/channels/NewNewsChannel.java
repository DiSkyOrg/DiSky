package info.itsthesky.disky.elements.properties.channels;

import info.itsthesky.disky.api.skript.action.GuildAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;

public class NewNewsChannel extends GuildAction<ChannelAction> {

    static {
        register(
                NewNewsChannel.class,
                ChannelAction.class,
                "news[( |-)]channel"
        );
    }

    @Override
    protected ChannelAction create(@NotNull Guild guild) {
        return guild.createNewsChannel("default channel");
    }

    @Override
    public String getNewType() {
        return "newschannel";
    }

    @Override
    public Class<? extends ChannelAction> getReturnType() {
        return ChannelAction.class;
    }
}
