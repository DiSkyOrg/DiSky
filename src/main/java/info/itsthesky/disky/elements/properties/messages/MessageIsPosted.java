package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Is Posted")
@Description("Return true if the message is posted, means sent in every guild that follow this news channel.")
@Examples("event-message is posted")
public class MessageIsPosted extends PropertyCondition<Message> {

    static {
                register(
                MessageIsPosted.class,
                PropertyType.BE,
                "(publish|post|crosspost)ed",
                "message"
        );
    }

    @Override
    public boolean check(@NotNull Message msg) {
        return msg.getFlags().contains(Message.MessageFlag.CROSSPOSTED);
    }

    @Override
    protected String getPropertyName() {
        return "published";
    }
}
