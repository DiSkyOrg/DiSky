package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.PropertyCondition;
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
