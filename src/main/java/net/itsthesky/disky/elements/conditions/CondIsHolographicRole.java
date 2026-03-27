package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Role;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Is Holographic Role")
@Description("Check if a role has holographic colors enabled. This is a boosted guild feature.")
@Examples({
    "if {_role} has holographic colors:",
    "    send \"This role is holographic!\""
})
public class CondIsHolographicRole extends Condition {

    static {
        DiSkyRegistry.registerCondition(
                CondIsHolographicRole.class,
                Condition.ConditionType.COMBINED,
                "%role% (has|is) holographic [color[s]]",
                "%role% (doesn't have|is not|isn't) holographic [color[s]]"
        );
    }

    private Expression<Role> exprRole;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprRole = (Expression<Role>) exprs[0];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(@NotNull Event e) {
        final Role role = EasyElement.parseSingle(exprRole, e, null);
        if (role == null)
            return isNegated();

        final var colors = role.getColors();
        final boolean isHolographic = colors != null && colors.isHolographic();
        return isNegated() != isHolographic;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return exprRole.toString(e, debug) + (isNegated() ? " doesn't have" : " has") + " holographic colors";
    }
}
