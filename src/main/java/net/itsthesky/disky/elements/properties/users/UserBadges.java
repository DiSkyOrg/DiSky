package net.itsthesky.disky.elements.properties.users;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

@Name("User Badges")
@Description({"Represent every badges' name a user have.",
"This does not and cannot contain any intro-related badges (nitro membership or nitro boosting), it need OAuth2 to be enabled."})
@Examples("reply with \"Whoa! You got all of them? %join badges of event-user with nl%\"")
public class UserBadges extends MultipleUserProperty<String> {

    static {
        register(
                UserBadges.class,
                String.class,
                "badge[s]"
        );
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String[] convert(User user) {
        return user.getFlags().stream()
                .map(User.UserFlag::getName)
                .toArray(String[]::new);
    }
}
