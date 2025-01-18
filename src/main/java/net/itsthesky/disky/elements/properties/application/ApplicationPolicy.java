package net.itsthesky.disky.elements.properties.application;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplicationPolicy extends SimplePropertyExpression<ApplicationInfo, String> {

    static {
        register(ApplicationPolicy.class, String.class,
                "[discord] [application] [privacy] policy [url]",
                "applicationinfo"
        );
    }

    @Override
    public @Nullable String convert(ApplicationInfo applicationInfo) {
        return applicationInfo.getPrivacyPolicyUrl();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "application privacy policy";
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
