package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.itsthesky.disky.api.generator.SeeAlso;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Member Has Role")
@Description("Check if a member has a specific role.")
@Examples({"if event-member has discord role with id \"000\":"})
@Since("4.0.0")
@SeeAlso({Member.class, Role.class})
public class HasRole extends Condition {

    static {
        Skript.registerCondition(HasRole.class,
                "%members% (has|have) discord [role] %role%",
                "%members% (doesn't|don't|does not|do not) have discord [role] %role%"
        );
    }

    private Expression<Member> exprMembers;
    private Expression<Role> exprRoles;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern,
                        @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprMembers = (Expression<Member>) exprs[0];
        exprRoles = (Expression<Role>) exprs[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        return exprMembers.check(event, member ->
                exprRoles.check(event, role ->
                        member.getRoles().contains(role)
                ), isNegated());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return exprMembers.toString(event, debug)
                + (isNegated() ? " doesn't have" : " has")
                + " discord role " + exprRoles.toString(event, debug);
    }
}
