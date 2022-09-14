package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceUpdateEvent;

public class MemberVoiceJoinEvent extends DiSkyEvent<GenericGuildVoiceUpdateEvent> {

    static {
        register("Member Voice Join Event", MemberVoiceJoinEvent.class, BukkitMemberVoiceJoinEvent.class,
                "[discord] [member] voice [channel] join")
                .description("Fired when a member joins a voice or a stage channel, also fires when a member moves to another channel")
                .examples("on voice channel join:");

        SkriptUtils.registerBotValue(BukkitMemberVoiceJoinEvent.class);

        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, AudioChannel.class,
                event -> event.getJDAEvent().getChannelJoined(), 1);
        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, AudioChannel.class,
                event -> event.getJDAEvent().getChannelJoined(), 0);
        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, AudioChannel.class,
                event -> event.getJDAEvent().getChannelLeft(), -1);

        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, VoiceChannel.class,
                event -> event.getJDAEvent().getChannelJoined() instanceof VoiceChannel ? (VoiceChannel) event.getJDAEvent().getChannelJoined() : null, 1);
        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, VoiceChannel.class,
                event -> event.getJDAEvent().getChannelJoined() instanceof VoiceChannel ? (VoiceChannel) event.getJDAEvent().getChannelJoined() : null, 0);
        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, VoiceChannel.class,
                event -> event.getJDAEvent().getChannelLeft() instanceof VoiceChannel ? (VoiceChannel) event.getJDAEvent().getChannelLeft() : null, -1);

        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, StageChannel.class,
                event -> event.getJDAEvent().getChannelJoined() instanceof StageChannel ? (StageChannel) event.getJDAEvent().getChannelJoined() : null, 1);
        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, StageChannel.class,
                event -> event.getJDAEvent().getChannelJoined() instanceof StageChannel ? (StageChannel) event.getJDAEvent().getChannelJoined() : null, 0);
        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, StageChannel.class,
                event -> event.getJDAEvent().getChannelLeft() instanceof StageChannel ? (StageChannel) event.getJDAEvent().getChannelLeft() : null, -1);

        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, Member.class,
                event -> event.getJDAEvent().getMember());
    }

    public static class BukkitMemberVoiceJoinEvent extends SimpleDiSkyEvent<GenericGuildVoiceUpdateEvent> {
        public BukkitMemberVoiceJoinEvent(MemberVoiceJoinEvent event) {
        }
    }
    
}
