package net.itsthesky.disky.elements.events.member;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public class MemberRemoveEvent extends DiSkyEvent<GuildMemberRemoveEvent> {

    static {
        register("Member Leave Event", MemberRemoveEvent.class, BukkitMemberRemoveEvent.class,
                "[discord] member (leave|left) [guild]")
                .description("Fired when a member is removed from a guild either by leaving or being punished. Use the ban/kick event instead to check the exact reason")
                .examples("on member leave:");


        SkriptUtils.registerBotValue(MemberRemoveEvent.BukkitMemberRemoveEvent.class);

        SkriptUtils.registerValue(MemberRemoveEvent.BukkitMemberRemoveEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(MemberRemoveEvent.BukkitMemberRemoveEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberRemoveEvent extends SimpleDiSkyEvent<GuildMemberRemoveEvent> {
        public BukkitMemberRemoveEvent(MemberRemoveEvent event) {
        }
    }
}
