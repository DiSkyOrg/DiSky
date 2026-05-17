package net.itsthesky.disky.elements.properties.polls;

import lombok.Getter;
import net.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class PollAnswerData {

    public @NotNull String answer;
    public @Nullable Emote emote;

    // Final poll answers data
    public int votes = 0;
    public boolean selfVote = false;

    /**
     * The numeric ID of this answer within its poll. This is the 1-indexed
     * position of the answer (first is 1) and is required for REST calls like
     * Message#retrievePollVoters(long).
     *
     * <p>Only populated when this PollAnswerData was built from an existing
     * {@link MessagePoll.Answer} (i.e. a poll that has been sent). For
     * builder-side answers created via the new-poll-answer expression, this
     * remains 0 because the ID is not assigned until the poll is sent.
     */
    public long id = 0L;

    public PollAnswerData(@NotNull String answer, @Nullable Emote emote) {
        this.answer = answer;
        this.emote = emote;
    }

    public PollAnswerData(@NotNull MessagePoll.Answer answer) {
        this.id = answer.getId();
        this.answer = answer.getText();
        this.emote = Emote.fromUnion(answer.getEmoji());
        this.votes = answer.getVotes();
        this.selfVote = answer.isSelfVoted();
    }

    public @Nullable Emoji getJDAEmote() {
        return emote == null ? null : emote.getEmoji();
    }

    public boolean isValid() {
        return !answer.isEmpty();
    }

    public String asString() {
        return answer + (emote == null ? "" : " " + emote.getName());
    }
}