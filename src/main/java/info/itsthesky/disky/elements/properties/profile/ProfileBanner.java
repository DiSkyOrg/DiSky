package info.itsthesky.disky.elements.properties.profile;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Profile Banner")
@Description({"Get the profile banner URL. If the user doesn't have a custom banner, this will return none.",
"Use the 'profile color' expression to get the color instead of the banner URL in that case!"})
public class ProfileBanner extends SimplePropertyExpression<User.Profile, String> {

    static {
        register(
                ProfileBanner.class,
                String.class,
                "profile banner [ur(l|i)]",
                "userprofile"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "profile banner";
    }

    @Override
    public @Nullable String convert(User.Profile profile) {
        return profile.getBannerUrl();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
