package net.itsthesky.disky.elements.properties.channels;

import net.itsthesky.disky.api.skript.action.GuildAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;

public class NewVoiceAction extends GuildAction<ChannelAction> {

    static {
        register(
                NewVoiceAction.class,
                ChannelAction.class,
                "voice[( |-)]channel"
        );
    }

    @Override
    protected ChannelAction create(@NotNull Guild guild) {
        return guild.createVoiceChannel("default channel");
    }

    @Override
    public String getNewType() {
        return "voicechannel";
    }

    @Override
    public Class<? extends ChannelAction> getReturnType() {
        return ChannelAction.class;
    }
}
