package net.itsthesky.disky.elements.properties.ban;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Ban User")
@Description("The user linked to this ban.")
public class BanUser extends SimplePropertyExpression<Guild.Ban, User> {

    static {
        register(
                BanUser.class,
                User.class,
                "[banned] user",
                "ban"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "user";
    }

    @Override
    public @Nullable User convert(Guild.Ban ban) {
        return ban.getUser();
    }

    @Override
    public @NotNull Class<? extends User> getReturnType() {
        return User.class;
    }
}
