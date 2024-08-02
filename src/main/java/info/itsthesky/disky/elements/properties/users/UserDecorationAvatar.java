package info.itsthesky.disky.elements.properties.users;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserDecorationAvatar extends UserProperty<String> {

    static {
        /*register(
                UserDecorationAvatar.class,
                String.class,
                "[user] (decoration[s] avatar|avatar decoration[s])"
        );*/
    }

    @Override
    public @Nullable String convert(User from) {
        return null;
        //return from.getAvatarDecoration() == null ? null : from.getAvatarDecoration().getDecorationAvatarUrl();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
