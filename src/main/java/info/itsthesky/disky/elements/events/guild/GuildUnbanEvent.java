package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;

public class GuildUnbanEvent extends DiSkyEvent<net.dv8tion.jda.api.events.guild.GuildUnbanEvent> {

    static {
        register("Guild Unban Event", GuildUnbanEvent.class, BukkitGuildUnbanEvent.class,
                "[discord] guild [user] unban)")
                .description("Fired when a user is unbanned \n can be used to get the unbanned user, the author and the guild.")
                .examples("on guild unban:");


        SkriptUtils.registerBotValue(GuildUnbanEvent.BukkitGuildUnbanEvent.class);

        SkriptUtils.registerAuthorValue(GuildUnbanEvent.BukkitGuildUnbanEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildUnbanEvent.BukkitGuildUnbanEvent.class, User.class,
                event -> event.getJDAEvent().getUser(), 0);

        SkriptUtils.registerValue(GuildUnbanEvent.BukkitGuildUnbanEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildUnbanEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.guild.GuildUnbanEvent> {
        public BukkitGuildUnbanEvent(GuildUnbanEvent event) {
        }
    }
}