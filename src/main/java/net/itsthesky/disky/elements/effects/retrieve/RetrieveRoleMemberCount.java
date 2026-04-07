package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleMemberCounts;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Role Member Count")
@Description({
    "Returns the member count for a specific role. This will act differently, whether **await** is used or not:",
    "",
    "- If **await** is used, the member count will be retrieved asynchronously from Discord, giving you the accurate, real-time count.",
    "- If **await** is __not__ used, this expression will return 0, as member counts cannot be cached."
})
@Examples({
    "await set {_count} to role member count of {_role} # will ask Discord for the accurate count. Recommended!",
    "set {_count} to role member count of {_role} # will return 0, as member counts are not cached"
})
@Since("4.27.0")
public class RetrieveRoleMemberCount extends SimpleExpression<Number> implements IAsyncGettableExpression<Number> {

    static {
        DiSkyRegistry.registerExpression(
                RetrieveRoleMemberCount.class,
                Number.class,
                ExpressionType.COMBINED,
                "[the] role member count of %role%"
        );
    }

    private Expression<Role> exprRole;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        exprRole = (Expression<Role>) expressions[0];
        return true;
    }

    @Override
    public Number[] getAsync(Event e) {
        return getMemberCount(e, true);
    }

    @Override
    protected Number @Nullable [] get(Event event) {
        return getMemberCount(event, false);
    }

    private Number @NotNull [] getMemberCount(Event event, boolean async) {
        final Role role = EasyElement.parseSingle(exprRole, event, null);
        if (EasyElement.anyNull(this, role))
            return new Number[0];

        if (async) {
            try {
                final RoleMemberCounts counts = role.getGuild().retrieveRoleMemberCounts().complete();
                return new Number[]{counts.get(role)};
            } catch (Exception ex) {
                DiSkyRuntimeHandler.error(ex);
                return new Number[0];
            }
        } else {
            // Cannot get member count from cache
            return new Number[]{0};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "role member count of " + exprRole.toString(event, debug);
    }
}
