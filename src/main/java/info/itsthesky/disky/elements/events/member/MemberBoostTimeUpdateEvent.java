package info.itsthesky.disky.elements.events.member;

import ch.njol.skript.util.Date;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;

public class MemberBoostTimeUpdateEvent extends DiSkyEvent<GuildMemberUpdateBoostTimeEvent> {

    static {
        register("Member Boost Time Update Event", MemberBoostTimeUpdateEvent.class, BukkitMemberBoostTimeUpdateEvent.class,
                "[discord] [guild] member boost time (change|update)");

        SkriptUtils.registerBotValue(BukkitMemberBoostTimeUpdateEvent.class);

        SkriptUtils.registerValue(BukkitMemberBoostTimeUpdateEvent.class, Date.class,
                event -> SkriptUtils.convertDateTime(event.getJDAEvent().getOldValue()), -1);

        SkriptUtils.registerValue(BukkitMemberBoostTimeUpdateEvent.class, Date.class,
                event -> SkriptUtils.convertDateTime(event.getJDAEvent().getNewValue()), 0);

        SkriptUtils.registerValue(BukkitMemberBoostTimeUpdateEvent.class, Date.class,
                event -> SkriptUtils.convertDateTime(event.getJDAEvent().getNewValue()), 1);

        SkriptUtils.registerValue(BukkitMemberBoostTimeUpdateEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitMemberBoostTimeUpdateEvent.class, Member.class,
                event -> event.getJDAEvent().getMember());

        SkriptUtils.registerValue(BukkitMemberBoostTimeUpdateEvent.class, User.class,
                event -> event.getJDAEvent().getUser());
    }

    public static class BukkitMemberBoostTimeUpdateEvent extends SimpleDiSkyEvent<GuildMemberUpdateBoostTimeEvent> {
        public BukkitMemberBoostTimeUpdateEvent(MemberBoostTimeUpdateEvent event) {
        }
    }
}
