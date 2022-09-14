package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSplashEvent;

public class GuildSplashEvent extends DiSkyEvent<GuildUpdateSplashEvent> {

    static {
        register("Guild Splash Event", GuildSplashEvent.class, BukkitGuildSplashEvent.class,
                "[discord] guild splash (change|update)")
                .description("Fired when a banner of a guild changes can be used to get the old/new banner, the author and the guild.")
                .examples("on guild splash change:");


        SkriptUtils.registerBotValue(GuildSplashEvent.BukkitGuildSplashEvent.class);

        SkriptUtils.registerAuthorValue(GuildSplashEvent.BukkitGuildSplashEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildSplashEvent.BukkitGuildSplashEvent.class, String.class,
                event -> event.getJDAEvent().getOldSplashUrl(), -1);

        SkriptUtils.registerValue(GuildSplashEvent.BukkitGuildSplashEvent.class, String.class,
                event -> event.getJDAEvent().getNewSplashUrl(), 0);

        SkriptUtils.registerValue(GuildSplashEvent.BukkitGuildSplashEvent.class, String.class,
                event -> event.getJDAEvent().getNewSplashUrl(), 1);

        SkriptUtils.registerValue(GuildSplashEvent.BukkitGuildSplashEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildSplashEvent extends SimpleDiSkyEvent<GuildUpdateSplashEvent> {
        public BukkitGuildSplashEvent(GuildSplashEvent event) {
        }
    }
}
