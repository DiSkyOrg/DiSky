package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfMuteEvent;

public class MemberSelfMuteEvent extends DiSkyEvent<GuildVoiceSelfMuteEvent> {
    static {
        register("Member Self Mute Event", MemberSelfMuteEvent.class, MemberSelfMuteEvent.BukkitMemberSelfMuteEvent.class,
                "[discord] member [self] [un]mute[d]")
                .description("Fired when a member mutes or unmutes themselves")
                .examples("on member mute:" +
                        "\n\tbroadcast event-boolean, event-member and event-guild");

        SkriptUtils.registerBotValue(BukkitMemberSelfMuteEvent.class);

        SkriptUtils.registerValue(BukkitMemberSelfMuteEvent.class, Boolean.class,
                event -> event.getJDAEvent().isSelfMuted(), 0);

        SkriptUtils.registerValue(BukkitMemberSelfMuteEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(BukkitMemberSelfMuteEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberSelfMuteEvent extends SimpleDiSkyEvent<GuildVoiceSelfMuteEvent> {
        public BukkitMemberSelfMuteEvent(MemberSelfMuteEvent event) {
        }
    }
}
