package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

@Name("Get Role")
@Description({"Get a role from a guild using its unique ID.",
        "Role are global on discord, means two role from two different guild could never have the same ID.",
        "This expression cannot be changed."})
@Examples("role with id \"000\"")
public class GetRole extends BaseGetterExpression<Role> {

    static {
        register(GetRole.class,
                Role.class,
                "role");
    }

    @Override
    protected Role get(String id, Bot bot) {
        return bot.getInstance().getRoleById(id);
    }

    @Override
    public String getCodeName() {
        return "role";
    }

    @Override
    public @NotNull Class<? extends Role> getReturnType() {
        return Role.class;
    }
}
