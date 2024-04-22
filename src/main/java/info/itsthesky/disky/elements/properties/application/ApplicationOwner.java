package info.itsthesky.disky.elements.properties.application;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplicationOwner extends SimplePropertyExpression<ApplicationInfo, User> {

    static {
        register(ApplicationOwner.class, User.class,
                "[discord] application owner",
                "applicationinfo"
        );
    }

    @Override
    public @Nullable User convert(ApplicationInfo applicationInfo) {
        return applicationInfo.getOwner();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "application owner";
    }

    @Override
    public @NotNull Class<? extends User> getReturnType() {
        return User.class;
    }
}
