package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public class MemberRemoveEvent extends DiSkyEvent<GuildMemberRemoveEvent> {

    static {
        register("Member Remove Event", MemberRemoveEvent.class, BukkitMemberRemoveEvent.class,
                "[discord] member leave[ed] [guild]")
                .description("Fired when a member is removed from a guild either by leaving or being punished. Use the ban/kick event instead to check the exact reason")
                .examples("on member leave:");


        SkriptUtils.registerBotValue(MemberRemoveEvent.BukkitMemberRemoveEvent.class);

        SkriptUtils.registerValue(MemberRemoveEvent.BukkitMemberRemoveEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(MemberRemoveEvent.BukkitMemberRemoveEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberRemoveEvent extends SimpleDiSkyEvent<GuildMemberRemoveEvent> {
        public BukkitMemberRemoveEvent(MemberJoinEvent event) {
        }
    }
}
