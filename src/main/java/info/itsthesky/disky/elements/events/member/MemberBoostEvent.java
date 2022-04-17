package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;

import java.time.OffsetDateTime;

public class MemberBoostEvent extends DiSkyEvent<GuildMemberUpdateBoostTimeEvent> {

    static {
        register("Member Boost Event", MemberBoostEvent.class, BukkitMemberBoostEvent.class,
                "[discord] [guild] member boost (change|update)")
                .description("Fired when a member starts or stops boosting a guild \n can be used to get the old/new boosting time and the guild.")
                .examples("member boost change:");


        SkriptUtils.registerBotValue(MemberBoostEvent.BukkitMemberBoostEvent.class);

        SkriptUtils.registerAuthorValue(MemberBoostEvent.BukkitMemberBoostEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, OffsetDateTime.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, OffsetDateTime.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, OffsetDateTime.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(MemberBoostEvent.BukkitMemberBoostEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

    }

    public static class BukkitMemberBoostEvent extends SimpleDiSkyEvent<GuildMemberUpdateBoostTimeEvent> {
        public BukkitMemberBoostEvent(MemberBoostEvent event) {
        }
    }
}