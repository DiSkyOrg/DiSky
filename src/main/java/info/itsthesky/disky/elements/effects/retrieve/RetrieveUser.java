package info.itsthesky.disky.elements.effects.retrieve;

import info.itsthesky.disky.api.skript.BaseRetrieveEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

public class RetrieveUser extends BaseRetrieveEffect<User, Bot> {

    static {
        register(
                RetrieveUser.class,
                "user",
                "bot",
                true, false
        );
    }

    @Override
    protected boolean allowCustomBot() {
        return false;
    }

    @Override
    protected RestAction<User> retrieve(@NotNull String input, @NotNull Bot entity) {
        return entity.getInstance().retrieveUserById(input);
    }
}
