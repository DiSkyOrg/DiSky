package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message Jump URL")
@Description("Get the jump URL of the specific message.")
@Examples("jump url of event-message")
public class MessageJumpURL extends SimplePropertyExpression<Message, String> {

    static {
        register(
                MessageJumpURL.class,
                String.class,
                "[discord] [message] [jump] url",
                "message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "jump url";
    }

    @Override
    public @Nullable String convert(Message message) {
        return message.getJumpUrl();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
