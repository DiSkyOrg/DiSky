package info.itsthesky.disky.elements.properties.polls;

import info.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PollAnswerData {

    public @NotNull String answer;
    public @Nullable Emote emote;

    public PollAnswerData(@NotNull String answer, @Nullable Emote emote) {
        this.answer = answer;
        this.emote = emote;
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
}
