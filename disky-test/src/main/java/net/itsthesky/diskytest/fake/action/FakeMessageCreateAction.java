package net.itsthesky.diskytest.fake.action;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * A {@link FakeRestAction} whose proxy also declares {@link MessageCreateAction}.
 *
 * <p>This is required because DiSky's {@code PostMessage} assigns the return value
 * of {@code channel.sendMessage(...)} to a typed {@code MessageCreateAction} local
 * variable. Our proxy must declare the interface (even though we only need
 * {@code complete()} to actually work) or the cast throws a
 * {@link ClassCastException}.
 *
 * <p>Only the methods actually called by DiSky's {@code PostMessage} are
 * implemented; everything else throws via the proxy's default
 * {@link UnsupportedOperationException} path.
 */
public class FakeMessageCreateAction extends FakeRestAction<Message> {

    public FakeMessageCreateAction(JDA jda, Callable<Message> action) {
        super(jda, action, MessageCreateAction.class);
    }

    /** Return the proxy cast to the specific {@link MessageCreateAction} type. */
    @SuppressWarnings("unchecked")
    public MessageCreateAction typedMCA() {
        return (MessageCreateAction) asProxy();
    }

    // ===== MessageCreateAction methods called by PostMessage =====

    /**
     * Set the message reference — no-op in the fake (references have no in-memory effect).
     * Returns {@code this} typed as {@link MessageCreateAction}.
     */
    @NotNull
    public MessageCreateAction setMessageReference(@Nullable Message message) {
        return typedMCA();
    }

    @NotNull
    public MessageCreateAction setMessageReference(@Nullable String messageId) {
        return typedMCA();
    }

    @NotNull
    public MessageCreateAction setMessageReference(long messageId) {
        return typedMCA();
    }

    @NotNull
    public MessageCreateAction setContent(@Nullable String content) {
        return typedMCA();
    }

    @NotNull
    public MessageCreateAction setEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return typedMCA();
    }

    @NotNull
    public MessageCreateAction setTTS(boolean isTTS) {
        return typedMCA();
    }

    @NotNull
    public MessageCreateAction setSuppressEmbeds(boolean suppress) {
        return typedMCA();
    }

    @NotNull
    public MessageCreateAction setSuppressedNotifications(boolean suppressed) {
        return typedMCA();
    }

    // setPoll is NOT implemented here.
    // PostMessage guards the call with `if (builder.getPoll() != null)`, so it is
    // never invoked for plain text/embed messages. If a test ever sends a poll,
    // the proxy will throw UnsupportedOperationException pointing here.
}
