package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.BaseRetrieveEffect;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Name("Retrieve Profile")
@Description({"Retrieve the profile of the specified user.",
        "Profile represent mainly the banner of the user, could return the accent color if non set."})
public class RetrieveProfile extends BaseRetrieveEffect<User.Profile, User> {

    static {
        register(
                RetrieveProfile.class,
                "profile",
                "user"
        );
    }

    @Override
    protected RestAction<User.Profile> retrieve(@NotNull String input, @NotNull User entity) {
        return entity.retrieveProfile();
    }
}
