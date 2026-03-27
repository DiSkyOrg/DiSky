package net.itsthesky.disky.elements.properties.role;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Role;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Gradient Role Colors")
@Description({
    "Get or set the gradient colors of a role. Returns the primary and secondary colors.",
    "Gradient role colors are a boosted guild feature."
})
@Examples({
    "set {_colors::*} to gradient colors of {_role}",
    "set gradient colors of {_role} to red and blue"
})
public class RoleGradientColors extends SimpleExpression<Color> {

    static {
        DiSkyRegistry.registerExpression(
                RoleGradientColors.class,
                Color.class,
                ExpressionType.COMBINED,
                "gradient color[s] of %role%",
                "%role%'s gradient color[s]"
        );
    }

    private Expression<Role> exprRole;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprRole = (Expression<Role>) exprs[0];
        return true;
    }

    @Override
    protected Color @NotNull [] get(@NotNull Event e) {
        final Role role = EasyElement.parseSingle(exprRole, e, null);
        if (EasyElement.anyNull(this, role))
            return new Color[0];

        final var colors = role.getColors();
        if (colors == null || !colors.isGradient())
            return new Color[0];

        return new Color[] {
            SkriptUtils.convert(new java.awt.Color(colors.getPrimaryRaw())),
            SkriptUtils.convert(new java.awt.Color(colors.getSecondaryRaw()))
        };
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    public Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return new Class[] {Color[].class};
        return new Class[0];
    }

    @Override
    public void change(@NotNull Event e, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {
        final Role role = EasyElement.parseSingle(exprRole, e, null);
        if (role == null || delta == null || delta.length < 2)
            return;

        final Color primary = (Color) delta[0];
        final Color secondary = (Color) delta[1];

        role.getManager()
            .setGradientColors(primary.asBukkitColor().asRGB(), secondary.asBukkitColor().asRGB())
            .complete();
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "gradient colors of " + exprRole.toString(e, debug);
    }
}
