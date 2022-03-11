package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Is Ephemeral")
@Description({"Return true if the message was ephemeral, e.g. private / hidden.",
"Action on hidden messages are limited."})
@Examples("event-message is ephemeral")
public class MessageIsEphemeral extends PropertyCondition<Message> {

    static {
        register(
                MessageIsEphemeral.class,
                PropertyType.BE,
                "ephemeral",
                "message"
        );
    }

    @Override
    public boolean check(@NotNull Message msg) {
        return msg.getFlags().contains(Message.MessageFlag.EPHEMERAL);
    }

    @Override
    protected String getPropertyName() {
        return "ephemeral";
    }
}
