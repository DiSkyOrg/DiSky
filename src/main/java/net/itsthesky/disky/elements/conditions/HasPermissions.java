package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.conditions.CondPermission;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.DiSkyRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Member Has Permissions")
@Description("Check if a member has permissions in an optional channel.")
@Examples({"if event-member has discord permission administrator: # global permission",
"if event-member has discord permission send message in event-channel: # channel specific permission"})
@Since("4.0.0")
@SeeAlso({Member.class, Permission.class, GuildChannel.class})
public class HasPermissions extends Condition {

    static {
        if (DiSkyRegistry.unregisterElement(SyntaxRegistry.CONDITION, CondPermission.class)) {
            Skript.registerCondition(
                    HasPermissions.class,
                    "%commandsenders/members% (has|have) [the] [discord] permission[s] %strings/permissions% [in [the] [channel] %-guildchannel%]",
                    "%commandsenders/members% (doesn't|does not|do not|don't) have [the] [discord] permission[s] %strings/permissions% [in [the] [channel] %-guildchannel%]"
            );
            DiSky.debug("Successfully unregistered the 'has permission' condition to register the new one.");
        } else {
            Skript.error("Cannot register the 'has permission' condition since the original one cannot be unregistered! We'll register the default one instead.");
            PropertyCondition.register(
                    HasPermissions.class,
                    PropertyCondition.PropertyType.HAVE,
                    "discord permission[s] %permissions% [in [the] [channel] %-guildchannel%]",
                    "member"
            );
        }
    }

    private Expression<Object> exprEntities;
    private Expression<Object> exprPerms;
    private Expression<GuildChannel> exprChannel;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprEntities = (Expression<Object>) exprs[0];
        exprPerms = (Expression<Object>) exprs[1];
        exprChannel = (Expression<GuildChannel>) exprs[2];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return exprEntities.check(event, rawEntity -> exprPerms.check(event, rawPermission -> {
            if (rawEntity == null || rawPermission == null)
                return isNegated();

            if (rawEntity instanceof Member member) {
                final Permission permissions = (Permission) rawPermission;

                final @Nullable GuildChannel channel = exprChannel == null ? null : exprChannel.getSingle(event);
                if (channel == null)
                    return member.hasPermission(permissions);
                else
                    return member.hasPermission(channel, permissions);

            } else if (rawEntity instanceof CommandSender s) {

                final String perm = (String) rawPermission;

                if (s.hasPermission(perm))
                    return true;
                // player has perm skript.foo.bar if he has skript.foo.* or skript.*,
                // but not for other plugin's permissions since they can define their own *
                if (perm.startsWith("skript.")) {
                    for (int i = perm.lastIndexOf('.'); i != -1; i = perm.lastIndexOf('.', i - 1)) {
                        if (s.hasPermission(perm.substring(0, i + 1) + "*"))
                            return true;
                    }
                }

            }
            return false;
        }), isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return exprEntities.toString(event, debug) + " has permission " + exprPerms.toString(event, debug) + (exprChannel == null ? "" : " in channel " + exprChannel.toString(event, debug));
    }
}
