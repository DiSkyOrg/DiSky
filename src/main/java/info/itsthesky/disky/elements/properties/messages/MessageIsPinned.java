package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Is Pinned")
@Description("Return true if the message is pinned.")
@Examples("event-message is pinned")
public class MessageIsPinned extends PropertyCondition<Message> {

    static {
        register(
                MessageIsPinned.class,
                PropertyType.BE,
                "pin[ned]",
                "message"
        );
    }

    @Override
    public boolean check(@NotNull Message msg) {
        return msg.isPinned();
    }

    @Override
    protected String getPropertyName() {
        return "pinned";
    }
}
