package net.itsthesky.disky.elements.events.members;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
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
public class MemberKickEvent extends SkriptEvent {

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
                event -> event.author);
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

    public static class BukkitMemberKickEvent extends BukkitMemberRemoveEvent {
        public BukkitMemberKickEvent(User target, Guild guild, JDA bot) {
            super(target, guild, bot);
        }
    }
}