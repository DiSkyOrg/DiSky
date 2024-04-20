package info.itsthesky.disky.elements.properties.polls;

import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import org.jetbrains.annotations.NotNull;

public class PollIsExpired extends PropertyCondition<MessagePoll> {

    static {
        register(PollIsExpired.class,
                PropertyCondition.PropertyType.BE,
                "expired",
                "messagepoll"
        );
    }

    @Override
    public boolean check(@NotNull MessagePoll poll) {
        return poll.isExpired();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "expired";
    }

}
