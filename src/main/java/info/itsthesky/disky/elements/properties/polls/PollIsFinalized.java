package info.itsthesky.disky.elements.properties.polls;

import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import org.jetbrains.annotations.NotNull;

public class PollIsFinalized extends PropertyCondition<MessagePoll> {

    static {
        register(PollIsFinalized.class,
                PropertyType.BE,
                "finalized",
                "messagepoll"
        );
    }

    @Override
    public boolean check(@NotNull MessagePoll poll) {
        return poll.isFinalizedVotes();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "finalized";
    }

}
