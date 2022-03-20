package info.itsthesky.disky.api.emojis;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Emote implements IMentionable {
    private final String name;
    private net.dv8tion.jda.api.entities.Emote emote;
    private final boolean isEmote;
    private final String mention;
    private net.dv8tion.jda.api.entities.Emoji emoji;

    public Emote(String name, info.itsthesky.disky.api.emojis.Emoji emoji) {
        this.name = name.replaceAll(":", "");
        this.mention = emoji.unicode();
        this.isEmote = false;
        this.emoji = Emoji.fromUnicode(emoji.unicode());
    }

    public static Emote fromReaction(MessageReaction.ReactionEmote emote) {
        if (emote.isEmote()) {
            return new Emote(emote.getEmote());
        } else {
            return new Emote(Emojis.ofUnicode(emote.getEmoji()).shortcodes().get(0), Emojis.ofUnicode(emote.getEmoji()));
        }
    }

    public static Emote[] convert(Collection<net.dv8tion.jda.api.entities.Emote> originals) {
        final List<Emote> e = new ArrayList<>();
        for (net.dv8tion.jda.api.entities.Emote em : originals)
            e.add(new Emote(em));
        return e.toArray(new Emote[0]);
    }

    public Emote(net.dv8tion.jda.api.entities.Emoji emoji) {
        this.emoji = emoji;
        this.mention = emoji.getName();
        this.name = emoji.getId();
        this.isEmote = false;
    }

    public static Emote fromActivityEmoji(net.dv8tion.jda.api.entities.Activity.Emoji emoji) {
        final Emoji temp;
        if (emoji.isEmote()) {
            temp = Emoji.fromEmote(emoji.getName(), emoji.getIdLong(), emoji.isAnimated());
        } else {
            temp = Emoji.fromUnicode(emoji.getAsCodepoints());
        }
        return new Emote(temp);
    }

    public static Emote fromJDA(net.dv8tion.jda.api.entities.Emote emote) {
        if (emote == null)
            return null;
        return new Emote(emote);
    }

    public RestAction<Void> addReaction(Message message) {
        if (isEmote)
            return message.addReaction(getEmote());
        else
            return message.addReaction(getEmoji().getName());
    }

    private Emote(net.dv8tion.jda.api.entities.Emote emote) {
        this.name = emote.getName();
        this.emote = emote;
        this.isEmote = true;
        this.mention = emote.getAsMention();
    }

    @Override
    public String toString() {
        return getName();
    }

    public Guild getGuild() {
        return isEmote ? emote.getGuild() : null;
    }

    public net.dv8tion.jda.api.entities.Emote getEmote() {
        return isEmote ? emote : null;
    }

    public List<Role> getRoles() {
        return isEmote ? emote.getRoles() : null;
    }

    public String getName() {
        return isEmote ? emote.getName() : name;
    }

    public JDA getJDA() {
        return isEmote ? emote.getJDA() : null;
    }

    @NotNull
    @Override
    public String getId() {
        return getID();
    }

    public boolean isEmote() {
        return isEmote;
    }

    public String getMention() {
        return mention;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public boolean isAnimated() {
        return isEmote && emote.isAnimated();
    }

    public String getID() {
        return isEmote ? emote.getId() : name;
    }

    @Override
    public @NotNull String getAsMention() {
        return isEmote ? emote.getAsMention() : mention;
    }

    @Override
    public long getIdLong() {
        return Long.parseLong(getID());
    }

	public Emoji asEmoji() {
        return isEmote() ? Emoji.fromEmote(getEmote()) : Emoji.fromUnicode(getAsMention());
	}

    public boolean isSimilar(Emote other) {
        if (other.isEmote && isEmote)
            return getEmote().getName().equals(other.getEmote().getName());
        else if (!other.isEmote && !isEmote)
            return getEmoji().getName().equals(other.getEmoji().getName());
        else
            return false;
    }

}