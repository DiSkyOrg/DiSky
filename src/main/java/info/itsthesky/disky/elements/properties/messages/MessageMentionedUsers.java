package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

@Name("Message Mentioned Users")
@Description("Get every mentioned users in a message.")
@Examples("mentioned users of event-message")
public class MessageMentionedUsers extends MultiplyPropertyExpression<Message, User> {

    static {
                register(
                MessageMentionedUsers.class,
                User.class,
                "[discord] [message] mentioned users",
                        "message"
        );
    }

    @Override
    public @NotNull Class<? extends User> getReturnType() {
        return User.class;
    }

    @Override
    protected String getPropertyName() {
        return "mentioned uers";
    }

    @Override
    protected User[] convert(Message message) {
        return message.getMentions().getUsers().toArray(new User[0]);
    }
}
