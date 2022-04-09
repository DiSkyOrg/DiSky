package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;

public class GuildInviteCreateEvent extends DiSkyEvent<net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent> {

    static {
        register("Invite Create Event", GuildInviteCreateEvent.class, BukkitInviteCreateEvent.class,
                "[discord] guild invite create")
                .description("Fired when a invite is created in a guild can be used to get the invite property, the author and the guild.")
                .examples("on guild invite create:");


        SkriptUtils.registerBotValue(GuildInviteCreateEvent.BukkitInviteCreateEvent.class);

        SkriptUtils.registerAuthorValue(GuildInviteCreateEvent.BukkitInviteCreateEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildInviteDeleteEvent.BukkitInviteDeleteEvent.class, Channel.class,
                event -> event.getJDAEvent().getChannel(), 0);

        SkriptUtils.registerValue(GuildInviteCreateEvent.BukkitInviteCreateEvent.class, Invite.class,
                event -> event.getJDAEvent().getInvite(), 0);

        SkriptUtils.registerValue(GuildInviteCreateEvent.BukkitInviteCreateEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitInviteCreateEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent> {
        public BukkitInviteCreateEvent(GuildInviteCreateEvent event) {
        }
    }
}
