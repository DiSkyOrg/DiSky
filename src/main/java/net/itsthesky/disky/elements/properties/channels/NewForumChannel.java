package net.itsthesky.disky.elements.properties.channels;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.itsthesky.disky.api.skript.action.GuildAction;
import org.jetbrains.annotations.NotNull;

public class NewForumChannel extends GuildAction<ChannelAction> {

   static {
       register(
               NewForumChannel.class,
               ChannelAction.class,
               "forum[( |-)]channel"
       );
   }


    @Override
    protected ChannelAction create(@NotNull Guild guild) {
        return guild.createForumChannel("default channel");
    }

    @Override
    public String getNewType() {
        return "forumchannel";
    }

    @Override
    public Class<? extends ChannelAction> getReturnType() {
        return ChannelAction.class;
    }
}
