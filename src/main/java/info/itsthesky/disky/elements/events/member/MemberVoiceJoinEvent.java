package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

public class MemberVoiceJoinEvent extends DiSkyEvent<GuildVoiceUpdateEvent> {

    @Override
    public boolean check(GuildVoiceUpdateEvent event) {
        return event.getChannelJoined() != null;
    }

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

    public static class BukkitMemberVoiceJoinEvent extends SimpleDiSkyEvent<GuildVoiceUpdateEvent> {
        public BukkitMemberVoiceJoinEvent(MemberVoiceJoinEvent event) {
        }
    }
    
}
