package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message Content")
@Description("Get the raw (non formatted) content of a sent message.")
@Examples("content of event-message")
public class MessageContent extends SimplePropertyExpression<Message, String> {

    static {
        register(
                MessageContent.class,
                String.class,
                "[discord] [message] content",
                "message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "content";
    }

    @Override
    public @Nullable String convert(Message message) {
        return message.getContentRaw();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
