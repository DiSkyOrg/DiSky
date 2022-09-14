package info.itsthesky.disky.elements.getters;

import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class GetUser extends BaseGetterExpression<User> {

    static {
        register(GetUser.class,
                TextChannel.class,
                "user");
    }

    @Override
    protected User get(String id, Bot bot) {
        return bot.getInstance().getUserById(id);
    }

    @Override
    public String getCodeName() {
        return "user";
    }

    @Override
    public @NotNull Class<? extends User> getReturnType() {
        return User.class;
    }
}
