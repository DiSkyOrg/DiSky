package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get User")
@Description({"Get a user from its unique ID.",
        "Users are global on Discord, different users cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("user with id \"000\"")
@Since("4.0.0")
@SeeAlso({User.class, TextChannel.class})
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

    @Override
    protected User getAsync(String id, Bot bot) {
        return bot.getInstance().retrieveUserById(id).complete();
    }
}
