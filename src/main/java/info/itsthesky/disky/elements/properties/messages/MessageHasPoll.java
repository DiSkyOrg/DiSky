package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.conditions.base.PropertyCondition;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

public class MessageHasPoll extends PropertyCondition<Message> {

    static {
                register(
                MessageHasPoll.class,
                PropertyType.HAVE,
                "poll",
                "message"
        );
    }

    @Override
    public boolean check(@NotNull Message msg) {
        return msg.getPoll() != null;
    }

    @Override
    protected String getPropertyName() {
        return "published";
    }
}
