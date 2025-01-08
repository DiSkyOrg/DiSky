package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class MemberJoinEvent extends DiSkyEvent<GuildMemberJoinEvent> {

    static {
        register("Member Join Event", MemberJoinEvent.class, BukkitMemberJoinEvent.class,
                "[discord] member join[ed] [guild]")
                .description("Fired when a member joins a guild.")
                .examples("on member join:");


        SkriptUtils.registerBotValue(MemberJoinEvent.BukkitMemberJoinEvent.class);

        SkriptUtils.registerValue(MemberJoinEvent.BukkitMemberJoinEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(MemberJoinEvent.BukkitMemberJoinEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberJoinEvent extends SimpleDiSkyEvent<GuildMemberJoinEvent> {
        public BukkitMemberJoinEvent(MemberJoinEvent event) {
        }
    }
}