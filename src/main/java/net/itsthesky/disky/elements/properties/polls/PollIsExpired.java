package net.itsthesky.disky.elements.properties.polls;

import ch.njol.skript.conditions.base.PropertyCondition;
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
