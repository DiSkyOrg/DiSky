package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkChannelEvent;

public class GuildAFKChannelEvent extends DiSkyEvent<GuildUpdateAfkChannelEvent> {

    static {
        register("Guild AFK Channel Event", GuildAFKChannelEvent.class, BukkitGuildAFKChannelEvent.class,
                "[discord] guild afk channel (change|update)")
                .description("Fired when a afk channel of a guild changes can be used to get the old/new channel, the author and the guild.")
                .examples("on guild afk channel change:");

        SkriptUtils.registerBotValue(GuildAFKChannelEvent.BukkitGuildAFKChannelEvent.class);

        SkriptUtils.registerAuthorValue(GuildAFKChannelEvent.BukkitGuildAFKChannelEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildAFKChannelEvent.BukkitGuildAFKChannelEvent.class, VoiceChannel.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(GuildAFKChannelEvent.BukkitGuildAFKChannelEvent.class, VoiceChannel.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(GuildAFKChannelEvent.BukkitGuildAFKChannelEvent.class, VoiceChannel.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(GuildAFKChannelEvent.BukkitGuildAFKChannelEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildAFKChannelEvent extends SimpleDiSkyEvent<GuildUpdateAfkChannelEvent> {
        public BukkitGuildAFKChannelEvent(GuildAFKChannelEvent event) {
        }
    }
}
