package net.itsthesky.disky.elements.properties.application;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplicationDescription extends SimplePropertyExpression<ApplicationInfo, String> {

    static {
        register(ApplicationDescription.class, String.class,
                "[discord] application description",
                "applicationinfo"
        );
    }

    @Override
    public @Nullable String convert(ApplicationInfo applicationInfo) {
        return applicationInfo.getDescription();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "application description";
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
