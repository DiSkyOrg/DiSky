package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

@Name("Message Mentioned Roles")
@Description("Get every mentioned Roles in a message. If the message doesn't come from a guild it will return an empty array!")
@Examples("mentioned roles of event-message")
public class MessageMentionedRoles extends MultiplyPropertyExpression<Message, Role> {

    static {
                register(
                MessageMentionedRoles.class,
                Role.class,
                "[discord] [message] mentioned roles",
                        "message"
        );
    }

    @Override
    public @NotNull Class<? extends Role> getReturnType() {
        return Role.class;
    }

    @Override
    protected String getPropertyName() {
        return "mentioned roles";
    }

    @Override
    protected Role[] convert(Message message) {
        return message.getMentions().getRoles().toArray(new Role[0]);
    }
}
