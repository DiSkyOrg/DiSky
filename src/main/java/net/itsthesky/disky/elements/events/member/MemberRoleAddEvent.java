package net.itsthesky.disky.elements.events.member;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;

public class MemberRoleAddEvent extends DiSkyEvent<GuildMemberRoleAddEvent> {

    static {
        register("Role Add Event", MemberRoleAddEvent.class, BukkitMemberRoleAddEvent.class,
                "[discord] [member] role add[ed]")
                .description("Fired when a member adds roles to another member, it's a log action so event-author returns who made the action event-roles returns a list of added roles")
                .examples("on role add:");


        SkriptUtils.registerBotValue(MemberRoleAddEvent.BukkitMemberRoleAddEvent.class);

        SkriptUtils.registerValues(MemberRoleAddEvent.BukkitMemberRoleAddEvent.class, Role.class,
                "roles", e -> e.getJDAEvent().getRoles().toArray(new Role[0]));

        SkriptUtils.registerValue(MemberRoleAddEvent.BukkitMemberRoleAddEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(MemberRoleAddEvent.BukkitMemberRoleAddEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberRoleAddEvent extends SimpleDiSkyEvent<GuildMemberRoleAddEvent> {
        public BukkitMemberRoleAddEvent(MemberRoleAddEvent event) {
        }
    }
}
