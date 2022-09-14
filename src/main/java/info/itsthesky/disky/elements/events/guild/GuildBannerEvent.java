package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBannerEvent;

public class GuildBannerEvent extends DiSkyEvent<GuildUpdateBannerEvent> {

    static {
        register("Guild Banner Event", GuildBannerEvent.class, BukkitGuildBannerEvent.class,
                "[discord] guild banner (change|update)")
                .description("Fired when a banner of a guild changes can be used to get the old/new banner, the author and the guild.")
                .examples("on guild banner change:");


        SkriptUtils.registerBotValue(GuildBannerEvent.BukkitGuildBannerEvent.class);

        SkriptUtils.registerAuthorValue(GuildBannerEvent.BukkitGuildBannerEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildBannerEvent.BukkitGuildBannerEvent.class, String.class,
                event -> event.getJDAEvent().getOldBannerUrl(), -1);

        SkriptUtils.registerValue(GuildBannerEvent.BukkitGuildBannerEvent.class, String.class,
                event -> event.getJDAEvent().getNewBannerUrl(), 0);

        SkriptUtils.registerValue(GuildBannerEvent.BukkitGuildBannerEvent.class, String.class,
                event -> event.getJDAEvent().getNewBannerUrl(), 1);

        SkriptUtils.registerValue(GuildBannerEvent.BukkitGuildBannerEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildBannerEvent extends SimpleDiSkyEvent<GuildUpdateBannerEvent> {
        public BukkitGuildBannerEvent(GuildBannerEvent event) {
        }
    }
}
