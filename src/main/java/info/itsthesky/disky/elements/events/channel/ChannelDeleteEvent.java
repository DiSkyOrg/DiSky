package info.itsthesky.disky.elements.events.channel;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;

public class ChannelDeleteEvent extends DiSkyEvent<net.dv8tion.jda.api.events.channel.ChannelDeleteEvent> {

    static {
        register("Channel delete event", ChannelDeleteEvent.class, BukkitChannelDeleteEvent.class,
                "[discord] channel delet(e|ion)")
                .description("Fired when a channel is deleted.")
                .examples("on channel deletion:\n\tbroadcast \"%event-channel%, %event-guild%\"");


        SkriptUtils.registerBotValue(BukkitChannelDeleteEvent.class);

        SkriptUtils.registerValue(BukkitChannelDeleteEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(BukkitChannelDeleteEvent.class, Channel.class,
                event -> event.getJDAEvent().getChannel());

    }

    public static class BukkitChannelDeleteEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.channel.ChannelDeleteEvent> {
        public BukkitChannelDeleteEvent(ChannelDeleteEvent event) {
        }
    }
}