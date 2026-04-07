package net.itsthesky.disky.elements.effects.find;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Members with roles")
@Description({
        "Returns the members that have the specified role(s) in a guild. This will act differently, wether **await** is used or not:",
        "",
        "- If **await** is used, the members will be retrieved asynchronously from Discord, you're sure to have every member with the specified role(s) in the returned array.",
        "- If **await** is __not__ used, the members will be retrieved from cache (according to the configured cache policy), so some members with the specified role(s) may not be included in the returned array."
})
@Examples({
        "set {_role} to role with id \"123456789\"",
        "await set {_members::*} to members with role {_role} from event-guild # will ask discord for every members. Recommended!",
        "set {_members::*} to members with role {_role} from event-guild # will get members from cache, so some members may be missing."
})
@Since("4.28.0")
public class MembersWithRoles extends SimpleExpression<Member> implements IAsyncGettableExpression<Member> {

    static {
        DiSkyRegistry.registerExpression(
                MembersWithRoles.class,
                Member.class,
                ExpressionType.COMBINED,
                "members with [the] role[s] %roles% (of|from|in) [the] [guild] %guild%"
        );
    }

    private Expression<Role> exprRoles;
    private Expression<Guild> exprGuild;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprRoles = (Expression<Role>) expressions[0];
        exprGuild = (Expression<Guild>) expressions[1];
        return true;
    }

    @Override
    public Member[] getAsync(Event e) {
        return getMembers(e, true);
    }

    @Override
    protected Member @Nullable [] get(Event event) {
        return getMembers(event, false);
    }

    private Member @NotNull [] getMembers(Event event, boolean async) {
        final var rolesObjs = exprRoles.getArray(event);
        final var guild = exprGuild.getSingle(event);
        if (rolesObjs.length == 0 || guild == null) return new Member[0];

        if (async) {
            return guild.findMembersWithRoles(rolesObjs).get().toArray(new Member[0]);
        } else {
            return guild.getMembersWithRoles(rolesObjs).toArray(new Member[0]);
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Member> getReturnType() {
        return Member.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "members with roles " + exprRoles.toString(event, debug) + " of guild " + exprGuild.toString(event, debug);
    }
}
