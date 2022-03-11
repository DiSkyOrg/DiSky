package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Message Attachment")
@Description("Get every attachment as custom object of a message")
@Examples("attachments of event-message")
public class MessageAttachments extends MultiplyPropertyExpression<Message, Message.Attachment> {

    static {
        register(
                MessageAttachments.class,
                Message.Attachment.class,
                "[discord] [message] attachment[s]",
                "message"
        );
    }

    @Override
    public @NotNull Class<? extends Message.Attachment> getReturnType() {
        return Message.Attachment.class;
    }

    @Override
    protected String getPropertyName() {
        return "attachments";
    }

    @Override
    protected Message.Attachment[] convert(Message original) {
        return original.getAttachments().toArray(new Message.Attachment[0]);
    }
}
