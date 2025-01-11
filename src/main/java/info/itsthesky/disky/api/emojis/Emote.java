package info.itsthesky.disky.api.emojis;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

public class Emote implements IMentionable {

    private final CustomEmoji customEmoji;
    private final UnicodeEmoji unicodeEmoji;

    private final Emoji emoji;

    public Emote(Emoji emoji) {
        this.emoji = emoji;
        this.customEmoji = emoji.getType().equals(Emoji.Type.CUSTOM) ? (CustomEmoji) emoji : null;
        this.unicodeEmoji = emoji.getType().equals(Emoji.Type.UNICODE) ? (UnicodeEmoji) emoji : null;
    }

    public static Emote fromUnion(EmojiUnion emote) {
        if (emote == null)
            return null;

        if (emote.getType().equals(Emoji.Type.CUSTOM)) {
            return new Emote(emote.asCustom());
        } else {
            return new Emote(Emoji.fromUnicode(emote.getName()));
        }
    }

    public static Emote fromJDA(CustomEmoji emote) {
        if (emote == null)
            return null;
        return new Emote(emote);
    }

    public RestAction<Void> addReaction(Message message) {
        return message.addReaction(getEmoji());
    }

    @Override
    public String toString() {
        return getName();
    }

    public UnicodeEmoji getUnicodeEmoji() {
        return unicodeEmoji;
    }

    public RichCustomEmoji getEmote() {
        return customEmoji instanceof RichCustomEmoji ? (RichCustomEmoji) customEmoji : null;
    }

    public String getName() {
        return emoji.getName();
    }

    @NotNull
    @Override
    public String getId() {
        return getID();
    }

    public boolean isCustom() {
        return emoji.getType().equals(Emoji.Type.CUSTOM);
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public boolean isAnimated() {
        return customEmoji != null && customEmoji.isAnimated();
    }

    public String getID() {
        return isCustom() ? customEmoji.getId() : unicodeEmoji.getName();
    }

    public Guild getGuild() {
        return isCustom() && customEmoji instanceof RichCustomEmoji ? ((RichCustomEmoji) customEmoji).getGuild() : null;
    }

    @Override
    public @NotNull String getAsMention() {
        if (isCustom()) {
            return "<" + (isAnimated() ? "a" : "") + ":"
                    + getName() + ":" + getID() + ">";
        } else {
            return getName();
        }
    }

    @Override
    public long getIdLong() {
        return Long.parseLong(getID());
    }

    public boolean isSimilar(Emote other) {
        if (other.isCustom() && isCustom() && getEmote() != null && other.getEmote() != null)
            return getEmote().getName().equals(other.getEmote().getName());
        else if (!other.isCustom() && !isCustom() && getEmoji() != null && other.getEmoji() != null)
            return getEmoji().getName().equals(other.getEmoji().getName());
        else
            return false;
    }

}