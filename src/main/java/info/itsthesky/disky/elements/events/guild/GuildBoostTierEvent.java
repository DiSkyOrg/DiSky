package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;

public class GuildBoostTierEvent extends DiSkyEvent<net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent> {

    static {
        register("Guild Boost Tier Update", GuildBoostTierEvent.class, BukkitGuildBoostTierEvent.class,
                "[discord] guild boost tier (change|update)")
                .description("Fired when a boost tier of a guild changes - can be used to get the old/new tier, and the guild.")
                .examples("on guild boost tier change:");


        SkriptUtils.registerBotValue(GuildBoostTierEvent.BukkitGuildBoostTierEvent.class);

        SkriptUtils.registerAuthorValue(GuildBoostTierEvent.BukkitGuildBoostTierEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildBoostTierEvent.BukkitGuildBoostTierEvent.class, String.class,
                event -> event.getJDAEvent().getOldBoostTier().name(), -1);

        SkriptUtils.registerValue(GuildBoostTierEvent.BukkitGuildBoostTierEvent.class, String.class,
                event -> event.getJDAEvent().getNewBoostTier().name(), 0);

        SkriptUtils.registerValue(GuildBoostTierEvent.BukkitGuildBoostTierEvent.class, String.class,
                event -> event.getJDAEvent().getNewBoostTier().name(), 1);

        SkriptUtils.registerValue(GuildBoostTierEvent.BukkitGuildBoostTierEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildBoostTierEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent> {
        public BukkitGuildBoostTierEvent(GuildBoostTierEvent event) {
        }
    }
}

