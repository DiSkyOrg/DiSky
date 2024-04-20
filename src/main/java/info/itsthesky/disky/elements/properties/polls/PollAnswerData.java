package info.itsthesky.disky.elements.properties.polls;

import info.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PollAnswerData {

    public @NotNull String answer;
    public @Nullable Emote emote;

    // Final poll answers data
    public int votes = 0;
    public boolean selfVote = false;

    public PollAnswerData(@NotNull String answer, @Nullable Emote emote) {
        this.answer = answer;
        this.emote = emote;
    }

    public PollAnswerData(@NotNull MessagePoll.Answer answer) {
        this.answer = answer.getText();
        this.emote = Emote.fromUnion(answer.getEmoji());
        this.votes = answer.getVotes();
        this.selfVote = answer.isSelfVoted();
    }

    public @NotNull String getAnswer() {
        return answer;
    }

    public @Nullable Emote getEmote() {
        return emote;
    }

    public @Nullable Emoji getJDAEmote() {
        return emote == null ? null : emote.getEmoji();
    }

    public boolean isValid() {
        return answer != null && !answer.isEmpty();
    }

    public String asString() {
        return answer + (emote == null ? "" : " " + emote.getName());
    }

    public boolean isSelfVote() {
        return selfVote;
    }

    public int getVotes() {
        return votes;
    }
}
