package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;

public class MemberAvatarEvent extends DiSkyEvent<GuildMemberUpdateAvatarEvent> {

    static {
        register("Member Avatar Event", MemberJoinEvent.class, BukkitMemberUpdateAvatarEvent.class,
                "[discord] [guild] member avatar (change|update)")
                .description("Fired when a member changes their avatar.")
                .examples("on member avatar change:");


        SkriptUtils.registerBotValue(MemberAvatarEvent.BukkitMemberUpdateAvatarEvent.class);


        SkriptUtils.registerValue(MemberAvatarEvent.BukkitMemberUpdateAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getNewAvatarUrl(), 0);
        SkriptUtils.registerValue(MemberAvatarEvent.BukkitMemberUpdateAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getNewAvatarUrl(), 1);
        SkriptUtils.registerValue(MemberAvatarEvent.BukkitMemberUpdateAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getOldAvatarUrl(), -1);

        SkriptUtils.registerValue(MemberAvatarEvent.BukkitMemberUpdateAvatarEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(MemberAvatarEvent.BukkitMemberUpdateAvatarEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberUpdateAvatarEvent extends SimpleDiSkyEvent<GuildMemberUpdateAvatarEvent> {
        public BukkitMemberUpdateAvatarEvent(MemberAvatarEvent event) {
        }
    }
}