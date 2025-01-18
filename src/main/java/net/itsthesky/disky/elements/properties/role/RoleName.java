package net.itsthesky.disky.elements.properties.role;

import ch.njol.skript.classes.Changer;
import net.itsthesky.disky.api.skript.action.ActionProperty;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RoleName extends ActionProperty<Role, RoleAction, String> {

    static {
        register(
                RoleName.class,
                String.class,
                "role name",
                "role/roleaction"
        );
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "name";
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode.equals(Changer.ChangeMode.SET) ? new Class[] {String.class} : new Class[0];
    }

    @Override
    public void change(Role role, String value, boolean async) {
        var action = role.getManager().setName(value);
        if (async) action.complete();
        else action.queue();
    }

    @Override
    public RoleAction change(RoleAction action, String value) {
        return action.setName(value);
    }

    @Override
    public String get(Role role, boolean async) {
        return role.getName();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
