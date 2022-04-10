package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;

public class MemberVoiceJoinEvent extends DiSkyEvent<GuildVoiceJoinEvent> {

    static {
        register("Member Voice Join Event", MemberVoiceJoinEvent.class, BukkitMemberVoiceJoinEvent.class,
                "[discord] [member] voice [channel] join")
                .description("Fired when a member join a voice or stage channel",
                        "This is also fired if a member join a channel **and were in one**, basically when he change of channel.")
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
                event -> event.getJDAEvent().getChannelJoined() instanceof VoiceChannel ? (VoiceChannel) event.getJDAEvent().getChannelLeft() : null, -1);

        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, StageChannel.class,
                event -> event.getJDAEvent().getChannelJoined() instanceof StageChannel ? (StageChannel) event.getJDAEvent().getChannelJoined() : null, 1);
        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, StageChannel.class,
                event -> event.getJDAEvent().getChannelJoined() instanceof StageChannel ? (StageChannel) event.getJDAEvent().getChannelJoined() : null, 0);
        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, StageChannel.class,
                event -> event.getJDAEvent().getChannelJoined() instanceof StageChannel ? (StageChannel) event.getJDAEvent().getChannelLeft() : null, -1);

        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(BukkitMemberVoiceJoinEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberVoiceJoinEvent extends SimpleDiSkyEvent<GuildVoiceJoinEvent> {
        public BukkitMemberVoiceJoinEvent(MemberVoiceJoinEvent event) {
        }
    }
    
}
