package net.itsthesky.disky.elements.properties.role;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleColors;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Role Colors")
@Description("This represents a rich object containing all the color-related properties of a role, such as its primary color, gradient, holographic effect, and more.")
// TODO examples
@Since("4.28.0")
@SeeAlso(PropRoleColor.class)
public class PropRoleColors extends SimplePropertyExpression<Role, RoleColors>
        implements IAsyncChangeableExpression {

    static {
        register(
                PropRoleColors.class,
                RoleColors.class,
                "role colors",
                "role"
        );
    }

    @Override
    public @Nullable RoleColors convert(Role from) {
        return from.getColors();
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        return mode == Changer.ChangeMode.SET ? new Class[]{RoleColors.class} : null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        change(event, delta, mode, false);
    }

    @Override
    public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
        change(e, delta, mode, true);
    }

    private void change(Event e, Object[] delta, Changer.ChangeMode mode, boolean async) {
        if (!DiSkyRuntimeHandler.validateAsync(async, getNode()))
            return;

        final var role = getExpr().getSingle(e);
        if (role == null)
            return;

        if (mode == Changer.ChangeMode.SET) {
            final var colors = (RoleColors) delta[0];
            final var action = role.getManager().setColors(colors);

            if (async) action.complete();
            else action.queue();
        }
    }

    @Override
    protected String getPropertyName() {
        return "role colors";
    }

    @Override
    public Class<? extends RoleColors> getReturnType() {
        return RoleColors.class;
    }
}
