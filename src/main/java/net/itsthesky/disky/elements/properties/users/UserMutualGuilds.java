package net.itsthesky.disky.elements.properties.users;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

@Name("User Mutual Guilds")
@Description("Represent every guild that the bot and the user have in common.")
@Examples("reply with \"Oh boi, we have %size of mutual guilds event-user% mutual guilds!\"")
public class UserMutualGuilds extends MultipleUserProperty<Guild>  {

    static {
        register(
                UserMutualGuilds.class,
                Guild.class,
                "mutual[s] guild[s]"
        );
    }

    @Override
    public @NotNull Class<? extends Guild> getReturnType() {
        return Guild.class;
    }

    @Override
    protected Guild[] convert(User user) {
        return user.getMutualGuilds().toArray(new Guild[0]);
    }
}
