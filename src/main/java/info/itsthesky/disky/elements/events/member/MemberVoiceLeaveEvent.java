package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;

public class MemberVoiceLeaveEvent extends DiSkyEvent<GuildVoiceLeaveEvent> {

    static {
        register("Member Voice Leave Event", MemberVoiceLeaveEvent.class, BukkitMemberVoiceLeaveEvent.class,
                "[discord] [member] voice [channel] leave")
                .description("Fired when a member leaves a voice or a stage channel")
                .examples("on voice channel leave:");

        SkriptUtils.registerBotValue(BukkitMemberVoiceLeaveEvent.class);

        SkriptUtils.registerValue(BukkitMemberVoiceLeaveEvent.class, AudioChannel.class,
                event -> event.getJDAEvent().getChannelLeft());

        SkriptUtils.registerValue(BukkitMemberVoiceLeaveEvent.class, VoiceChannel.class,
                event -> event.getJDAEvent().getChannelLeft() instanceof VoiceChannel ? (VoiceChannel) event.getJDAEvent().getChannelLeft() : null);

        SkriptUtils.registerValue(BukkitMemberVoiceLeaveEvent.class, StageChannel.class,
                event -> event.getJDAEvent().getChannelLeft() instanceof StageChannel ? (StageChannel) event.getJDAEvent().getChannelLeft() : null);

        SkriptUtils.registerValue(BukkitMemberVoiceLeaveEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitMemberVoiceLeaveEvent.class, Member.class,
                event -> event.getJDAEvent().getMember());
    }

    public static class BukkitMemberVoiceLeaveEvent extends SimpleDiSkyEvent<GuildVoiceLeaveEvent> {
        public BukkitMemberVoiceLeaveEvent(MemberVoiceLeaveEvent event) {
        }
    }
    
}
