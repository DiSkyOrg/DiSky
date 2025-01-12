package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class GuildBanEvent extends DiSkyEvent<net.dv8tion.jda.api.events.guild.GuildBanEvent> {

    static {
        register("Guild Ban Event", GuildBanEvent.class, BukkitGuildBanEvent.class,
                "[discord] guild [user] ban")
                .description("Fired when a user is banned from a guild. A member doesn't exist here because the member is not in the guild anymore! Can be used to get the banned user, the author and the guild.")
                .examples("on guild ban:");


        SkriptUtils.registerBotValue(GuildBanEvent.BukkitGuildBanEvent.class);

        SkriptUtils.registerAuthorValue(GuildBanEvent.BukkitGuildBanEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildBanEvent.BukkitGuildBanEvent.class, User.class,
                event -> event.getJDAEvent().getUser(), 0);

        SkriptUtils.registerValue(GuildBanEvent.BukkitGuildBanEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildBanEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.guild.GuildBanEvent> {
        public BukkitGuildBanEvent(GuildBanEvent event) {
        }
    }
}
