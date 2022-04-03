package info.itsthesky.disky.elements.properties.users;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("User Discriminator")
@Description({"Represent the four digit number after the # of a user's name.",
        "These, mixed with the user name itself, are unique.",
        "This **DOES NOT** include the `#` char, so you have to add it yourself."})
@Examples("reply with discriminator of event-user")
public class UserDiscriminator extends UserProperty<String> {

    static {
        register(
                UserDiscriminator.class,
                String.class,
                "discriminator"
        );
    }

    @Override
    public @Nullable String convert(User user) {
        return user.getDiscriminator();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
