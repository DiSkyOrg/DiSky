package info.itsthesky.disky.elements.events.member;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ItsTheSky
 */
public class MemberBanEvent extends SkriptEvent {

    static {
        Skript.registerEvent("Member Ban Event",
                MemberBanEvent.class, BukkitMemberBanEvent.class,
                "[discord] member ban[ned]");

        SkriptUtils.registerValue(BukkitMemberBanEvent.class, User.class,
                event -> event.target);
        SkriptUtils.registerValue(BukkitMemberBanEvent.class, Guild.class,
                event -> event.guild);
        SkriptUtils.registerValue(BukkitMemberBanEvent.class, Bot.class,
                event -> Bot.byJDA(event.bot));
        SkriptUtils.registerValue(BukkitMemberBanEvent.class, Member.class,
                event -> event.author);
    }

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return event instanceof BukkitMemberBanEvent;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "member ban event";
    }

    public static class BukkitMemberBanEvent extends BukkitMemberRemoveEvent {
        public BukkitMemberBanEvent(User target, Guild guild, JDA bot) {
            super(target, guild, bot);
        }
    }
}
