package info.itsthesky.disky.elements.events.member;

import ch.njol.skript.util.Date;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;

import java.time.OffsetDateTime;

public class MemberBoostEvent extends DiSkyEvent<GuildMemberUpdateBoostTimeEvent> {

    static {
        register("Member Boost Time Update Event", MemberBoostEvent.class, BukkitMemberBoostEvent.class,
                "[discord] [guild] member boost time (change|update)");

        SkriptUtils.registerBotValue(MemberBoostEvent.BukkitMemberBoostEvent.class);

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, Date.class,
                event -> SkriptUtils.convertDateTime(event.getJDAEvent().getOldValue()), -1);

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, Date.class,
                event -> SkriptUtils.convertDateTime(event.getJDAEvent().getNewValue()), 0);

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, Date.class,
                event -> SkriptUtils.convertDateTime(event.getJDAEvent().getNewValue()), 1);

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, Member.class,
                event -> event.getJDAEvent().getMember());

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, User.class,
                event -> event.getJDAEvent().getUser());
    }

    public static class BukkitMemberBoostEvent extends SimpleDiSkyEvent<GuildMemberUpdateBoostTimeEvent> {
        public BukkitMemberBoostEvent(MemberBoostEvent event) {
        }
    }
}
