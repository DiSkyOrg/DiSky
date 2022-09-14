package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;

public class GuildBoostCountEvent extends DiSkyEvent<net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent> {

    static {
        register("Guild Boost Count Update", GuildBoostCountEvent.class, BukkitGuildBoostCountEvent.class,
                "[discord] guild boost count (change|update)")
                .description("Fired when a boost count of a guild changes - can be used to get the old/new count, and the guild.")
                .examples("on guild boost count change:");


        SkriptUtils.registerBotValue(GuildBoostCountEvent.BukkitGuildBoostCountEvent.class);

        SkriptUtils.registerAuthorValue(GuildBoostCountEvent.BukkitGuildBoostCountEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildBoostCountEvent.BukkitGuildBoostCountEvent.class, Integer.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(GuildBoostCountEvent.BukkitGuildBoostCountEvent.class, Integer.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(GuildBoostCountEvent.BukkitGuildBoostCountEvent.class, Integer.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(GuildBoostCountEvent.BukkitGuildBoostCountEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildBoostCountEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent> {
        public BukkitGuildBoostCountEvent(GuildBoostCountEvent event) {
        }
    }
}

