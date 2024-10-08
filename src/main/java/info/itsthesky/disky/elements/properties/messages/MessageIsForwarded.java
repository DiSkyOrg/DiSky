package info.itsthesky.disky.elements.properties.messages;

import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.MessageType;
import org.jetbrains.annotations.NotNull;

public class MessageIsForwarded extends PropertyCondition<Message> {

    static {
                register(
                MessageIsForwarded.class,
                PropertyType.BE,
                "forwarded",
                "message"
        );
    }

    @Override
    public boolean check(@NotNull Message msg) {
        return msg.getMessageReference() != null && msg.getMessageReference().getType().equals(MessageReference.MessageReferenceType.FORWARD);
    }

    @Override
    protected String getPropertyName() {
        return "published";
    }
}
