package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message User Author")
@Description("Get the user instance of the message's author. Can be null in case of the message was sent by a webhook.")
@Examples("author of event-message")
public class MessageAuthor extends SimplePropertyExpression<Message, User> {

    static {
        register(
                MessageAuthor.class,
                User.class,
                "[discord] [message] (user|author|writer)",
                "message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "author";
    }

    @Override
    public @Nullable User convert(Message message) {
        return message.getAuthor();
    }

    @Override
    public @NotNull Class<? extends User> getReturnType() {
        return User.class;
    }
}
