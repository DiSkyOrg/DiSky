package info.itsthesky.disky.elements.events.member;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;

public class MemberAcceptScreenEvent extends DiSkyEvent<GuildMemberUpdatePendingEvent> {

    static {
        register("Member Accept Screen Event", MemberAcceptScreenEvent.class, BukkitMemberUpdatePendingEvent.class,
                "[discord] [guild] member screen accept")
                .description("Fired when a member has agreed to membership screen requirements it can be useful for adding roles since the member is not available if they haven't accepted it yet.")
                .examples("on member screen accept:");


        SkriptUtils.registerBotValue(MemberAcceptScreenEvent.BukkitMemberUpdatePendingEvent.class);

        SkriptUtils.registerValue(MemberAcceptScreenEvent.BukkitMemberUpdatePendingEvent.class, Boolean.class,
                event -> event.getJDAEvent().getNewValue(), 0);
        SkriptUtils.registerValue(MemberAcceptScreenEvent.BukkitMemberUpdatePendingEvent.class, Boolean.class,
                event -> event.getJDAEvent().getNewValue(), 1);
        SkriptUtils.registerValue(MemberAcceptScreenEvent.BukkitMemberUpdatePendingEvent.class, Boolean.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(MemberAcceptScreenEvent.BukkitMemberUpdatePendingEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(MemberAcceptScreenEvent.BukkitMemberUpdatePendingEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitMemberUpdatePendingEvent extends SimpleDiSkyEvent<GuildMemberUpdatePendingEvent> {
        public BukkitMemberUpdatePendingEvent(MemberAcceptScreenEvent event) {
        }
    }
}
