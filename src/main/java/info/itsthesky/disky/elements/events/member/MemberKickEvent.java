package info.itsthesky.disky.elements.events.member;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

/**
 * @author ItsTheSky
 */
public class MemberKickEvent extends SkriptEvent {

    public static class MemberKickEventListener extends ListenerAdapter {

        private static final WeakHashMap<Long, BukkitMemberKickEvent> WaitingKicks = new WeakHashMap<>();

        @Override
        public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
            WaitingKicks.put(event.getGuild().getIdLong(),
                    new BukkitMemberKickEvent(event.getUser(), event.getGuild(), event.getJDA()));
        }

        @Override
        public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {
            if (event.getEntry().getType() == ActionType.KICK && WaitingKicks.containsKey(event.getGuild().getIdLong())) {
                final BukkitMemberKickEvent e = WaitingKicks.remove(event.getGuild().getIdLong());
                final JDA bot = e.bot;
                e.setAuthor(bot.getUserById(event.getEntry().getUserIdLong()));
                e.setAuthorMember(event.getGuild().getMemberById(event.getEntry().getUserIdLong()));
                SkriptUtils.dispatchEvent(e);
            }
        }
    }

    static {
        Skript.registerEvent("Member Kick Event",
                MemberKickEvent.class, BukkitMemberKickEvent.class,
                "[discord] member kick[ed]");

        SkriptUtils.registerValue(BukkitMemberKickEvent.class, User.class,
                event -> event.target);
        SkriptUtils.registerValue(BukkitMemberKickEvent.class, Guild.class,
                event -> event.guild);
        SkriptUtils.registerValue(BukkitMemberKickEvent.class, Bot.class,
                event -> Bot.byJDA(event.bot));
        SkriptUtils.registerValue(BukkitMemberKickEvent.class, Member.class,
                event -> event.authorMember);
    }

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return event instanceof BukkitMemberKickEvent;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "member kick event";
    }

    public static class BukkitMemberKickEvent extends Event {

        private final User target;
        private final Guild guild;
        private final JDA bot;
        private User author;
        private Member authorMember;

        private final static HandlerList handlers = new HandlerList();

        public BukkitMemberKickEvent(User target, Guild guild, JDA bot) {
            this.target = target;
            this.guild = guild;
            this.bot = bot;
        }

        public void setAuthor(User author) {
            this.author = author;
        }

        public void setAuthorMember(Member authorMember) {
            this.authorMember = authorMember;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }
        public static HandlerList getHandlerList() {
            return handlers;
        }
    }
}
