package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.EasyPropertyCondition;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Member Has Role")
@Description("Check if a member has a specific role.")
@Examples({"if event-member has discord role with id \"000\":"})
@Since("4.0.0")
public class HasRole extends EasyPropertyCondition<Member> {

    static {
        register(
                HasRole.class,
                PropertyCondition.PropertyType.HAVE,
                "discord [role] %role%",
                "member"
        );
    }

    private Expression<Role> exprRole;

    @Override
    public boolean check(Event e, Member entity) {
        final Role target = EasyElement.parseSingle(exprRole, e, null);
        if (target == null || entity == null)
            return false;
        return entity.getRoles().stream().anyMatch(role -> role.getId().equalsIgnoreCase(target.getId()));
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprRole = (Expression<Role>) exprs[1];
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }
}
