package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import org.jetbrains.annotations.NotNull;

@Name("Is Message Forwarded")
@Description("Check if a message is a 'forwarded message' or not, basically a message that was sent from another channel.")
@Examples({"if event-message is forwarded:",
        "if event-message is not forwarded:"})
@Since("4.20.0")
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
