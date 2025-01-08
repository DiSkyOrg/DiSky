package info.itsthesky.disky.core;

import info.itsthesky.disky.elements.events.member.MemberBanEvent;
import info.itsthesky.disky.elements.events.member.MemberKickEvent;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.WeakHashMap;

/**
 * Handler used to provide more information about a member remove event, e.g. kick or ban
 * @author ItsTheSky
 */
public class MemberRemoveEventListener extends ListenerAdapter {

    private final WeakHashMap<Long, MemberKickEvent.BukkitMemberKickEvent> WaitingKicks = new WeakHashMap<>();
    private final WeakHashMap<Long, MemberBanEvent.BukkitMemberBanEvent> WaitingBans = new WeakHashMap<>();

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        WaitingKicks.put(event.getGuild().getIdLong(),
                new MemberKickEvent.BukkitMemberKickEvent(event.getUser(), event.getGuild(), event.getJDA()));
        WaitingBans.put(event.getGuild().getIdLong(),
                new MemberBanEvent.BukkitMemberBanEvent(event.getUser(), event.getGuild(), event.getJDA()));
    }

    @Override
    public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {
        if (event.getEntry().getType() == ActionType.KICK && WaitingKicks.containsKey(event.getGuild().getIdLong())) {
            final MemberKickEvent.BukkitMemberKickEvent e = WaitingKicks.remove(event.getGuild().getIdLong());
            e.author = event.getGuild().getMemberById(event.getEntry().getUserIdLong());
            SkriptUtils.dispatchEvent(e);
        } else if (event.getEntry().getType() == ActionType.BAN && WaitingBans.containsKey(event.getGuild().getIdLong())) {
            final MemberBanEvent.BukkitMemberBanEvent e = WaitingBans.remove(event.getGuild().getIdLong());
            e.author = event.getGuild().getMemberById(event.getEntry().getUserIdLong());
            SkriptUtils.dispatchEvent(e);
        }
    }
}
