package info.itsthesky.disky.elements.properties.channels;

import info.itsthesky.disky.api.skript.action.GuildAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import org.jetbrains.annotations.NotNull;

public class NewThreadAction extends info.itsthesky.disky.api.skript.action.ChannelAction<ThreadChannelAction> {

    static {
        register(
                NewThreadAction.class,
                ChannelAction.class,
                "category"
        );
    }

    @Override
    protected ThreadChannelAction create(@NotNull TextChannel guild) {
        return guild.createThreadChannel("default name");
    }

    @Override
    public String getNewType() {
        return "category";
    }

    @Override
    public Class<? extends ThreadChannelAction> getReturnType() {
        return ThreadChannelAction.class;
    }
}
