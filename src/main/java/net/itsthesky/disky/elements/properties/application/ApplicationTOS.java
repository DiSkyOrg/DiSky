package net.itsthesky.disky.elements.properties.application;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplicationTOS extends SimplePropertyExpression<ApplicationInfo, String> {

    static {
        register(ApplicationTOS.class, String.class,
                "[discord] [application] (tos|terms of service) [url]",
                "applicationinfo"
        );
    }

    @Override
    public @Nullable String convert(ApplicationInfo applicationInfo) {
        return applicationInfo.getTermsOfServiceUrl();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "application tos";
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
