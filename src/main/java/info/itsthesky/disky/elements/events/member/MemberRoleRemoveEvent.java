package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;

public class MemberRoleRemoveEvent extends DiSkyEvent<GuildMemberRoleRemoveEvent> {

    static {
        register("Role Remove Event", MemberRoleRemoveEvent.class, BukkitMemberRemoveEvent.class,
                "[discord] [member] role remove[d]")
                .description("Fired when a member removes roles from another member, it's a log action so event-author returns who made the action event-roles returns a list of removed roles")
                .examples("on role remove:");


        SkriptUtils.registerBotValue(MemberRoleRemoveEvent.BukkitMemberRemoveEvent.class);

        SkriptUtils.registerValue(MemberRoleRemoveEvent.BukkitMemberRemoveEvent.class, Role[].class,
                event -> event.getJDAEvent().getRoles().toArray(new Role[0]), 0);

        SkriptUtils.registerValue(MemberRoleRemoveEvent.BukkitMemberRemoveEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(MemberRoleRemoveEvent.BukkitMemberRemoveEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberRemoveEvent extends SimpleDiSkyEvent<GuildMemberRoleRemoveEvent> {
        public BukkitMemberRemoveEvent(MemberRoleRemoveEvent event) {
        }
    }
}
