package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;

public class GuildInviteDeleteEvent extends DiSkyEvent<net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent> {

    static {
        register("Invite Delete Event", GuildInviteDeleteEvent.class, BukkitInviteDeleteEvent.class,
                "[discord] guild invite delete")
                .description("Fired when a invite is deleted from a guild can be used to get the invite property, the author and the guild.")
                .examples("on guild invite delete:");


        SkriptUtils.registerBotValue(GuildInviteDeleteEvent.BukkitInviteDeleteEvent.class);

        SkriptUtils.registerAuthorValue(GuildInviteDeleteEvent.BukkitInviteDeleteEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildInviteDeleteEvent.BukkitInviteDeleteEvent.class, Channel.class,
                event -> event.getJDAEvent().getChannel(), 0);

        SkriptUtils.registerValue(GuildInviteDeleteEvent.BukkitInviteDeleteEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitInviteDeleteEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent> {
        public BukkitInviteDeleteEvent(GuildInviteDeleteEvent event) {
        }
    }
}
