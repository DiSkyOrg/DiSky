package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfDeafenEvent;

public class MemberSelfDeafenEvent extends DiSkyEvent<GuildVoiceSelfDeafenEvent> {
    static {
        register("Member Self Deafen Event", MemberSelfDeafenEvent.class, MemberSelfDeafenEvent.BukkitMemberSelfDeafenEvent.class,
                "[discord] member [self] [un]deafen[ed]")
                .description("Fired when a member deafens or undeafens themselves")
                .examples("on member deafen:" +
                        "\n\tbroadcast event-boolean, event-member and event-guild");

        SkriptUtils.registerBotValue(BukkitMemberSelfDeafenEvent.class);

        SkriptUtils.registerValue(BukkitMemberSelfDeafenEvent.class, Boolean.class,
                event -> event.getJDAEvent().isSelfDeafened(), 0);

        SkriptUtils.registerValue(BukkitMemberSelfDeafenEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(BukkitMemberSelfDeafenEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberSelfDeafenEvent extends SimpleDiSkyEvent<GuildVoiceSelfDeafenEvent> {
        public BukkitMemberSelfDeafenEvent(MemberSelfDeafenEvent event) {
        }
    }
}
