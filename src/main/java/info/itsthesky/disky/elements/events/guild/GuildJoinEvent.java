package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;

public class GuildJoinEvent extends DiSkyEvent<net.dv8tion.jda.api.events.guild.GuildJoinEvent> {

    static {
        register("Guild Join Event", GuildJoinEvent.class, BukkitGuildJoinEvent.class,
                "[discord] bot join guild")
                .description("Fired when the bot joins in a guild.")
                .examples("on bot join guild:");


        SkriptUtils.registerBotValue(GuildJoinEvent.BukkitGuildJoinEvent.class);

        SkriptUtils.registerValue(GuildJoinEvent.BukkitGuildJoinEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildJoinEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.guild.GuildUnbanEvent> {
        public BukkitGuildJoinEvent(GuildJoinEvent event) {
        }
    }
}
