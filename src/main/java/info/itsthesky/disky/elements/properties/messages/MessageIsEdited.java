package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Is Edited")
@Description("Return true if the message was edited. Because of discord limitations, we cannot get the editing date.")
@Examples("event-message is edited")
public class MessageIsEdited extends PropertyCondition<Message> {

    static {
        register(
                MessageIsEdited.class,
                PropertyType.BE,
                "edited",
                "message"
        );
    }

    @Override
    public boolean check(@NotNull Message msg) {
        return msg.isEdited();
    }

    @Override
    protected String getPropertyName() {
        return "edited";
    }
}
