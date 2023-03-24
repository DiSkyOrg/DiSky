package info.itsthesky.disky.elements.events.channel;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;

public class ChannelCreateEvent extends DiSkyEvent<net.dv8tion.jda.api.events.channel.ChannelCreateEvent> {

    static {
        register("Channel create event", ChannelCreateEvent.class, BukkitChannelCreateEvent.class,
                "[discord] channel creat(e|ion)")
                .description("Fired when a channel is created.")
                .examples("on channel creation:\n\tbroadcast \"%event-channel%, %event-guild%\"");


        SkriptUtils.registerBotValue(BukkitChannelCreateEvent.class);

        SkriptUtils.registerValue(BukkitChannelCreateEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(BukkitChannelCreateEvent.class, Channel.class,
                event -> event.getJDAEvent().getChannel());

    }

    public static class BukkitChannelCreateEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.channel.ChannelCreateEvent> {
        public BukkitChannelCreateEvent(ChannelCreateEvent event) {
        }
    }
}