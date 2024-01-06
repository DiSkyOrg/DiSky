package info.itsthesky.disky.elements.properties.channels;

import info.itsthesky.disky.api.skript.action.GuildAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;

public class NewMediaChannel extends GuildAction<ChannelAction> {

    static {
        register(
                NewMediaChannel.class,
                ChannelAction.class,
                "media[( |-)]channel"
        );
    }

    @Override
    protected ChannelAction create(@NotNull Guild guild) {
        return guild.createMediaChannel("default channel");
    }

    @Override
    public String getNewType() {
        return "mediachannel";
    }

    @Override
    public Class<? extends ChannelAction> getReturnType() {
        return ChannelAction.class;
    }
}
