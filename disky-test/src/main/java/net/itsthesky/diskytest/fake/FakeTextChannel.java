package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.entities.channel.concrete.TextChannelImpl;
import net.itsthesky.diskytest.fake.action.FakeMessageCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Minimal stateful fake of a guild {@link TextChannel}.
 *
 * <p>This is the centerpiece of the loopback: every {@code sendMessage*} method
 * appends a {@link FakeMessage} to {@link #history} and dispatches a
 * {@link MessageReceivedEvent} through the fake JDA.
 */
public class FakeTextChannel extends FakeChannel<TextChannel> {

    private final Deque<FakeMessage> history = new ArrayDeque<>();

    public FakeTextChannel(FakeGuild guild, String name) {
        super(TextChannel.class, guild, name, allInterfacesOf(TextChannelImpl.class));
        guild.addFakeTextChannel(this);
    }

    public Deque<FakeMessage> getFakeHistory() { return history; }

    @NotNull
    @Override
    public ChannelType getType() { return ChannelType.TEXT; }

    // ===== Loopback message-send methods =====

    @NotNull
    public MessageCreateAction sendMessage(@NotNull CharSequence content) {
        return loopbackSend(content.toString(), Collections.emptyList());
    }

    @NotNull
    public MessageCreateAction sendMessage(@NotNull MessageCreateData msg) {
        String content = msg.getContent();
        return loopbackSend(content == null ? "" : content, msg.getEmbeds());
    }

    @NotNull
    public MessageCreateAction sendMessageEmbeds(@NotNull MessageEmbed embed,
                                                 @NotNull MessageEmbed... others) {
        List<MessageEmbed> all = new java.util.ArrayList<>();
        all.add(embed);
        Collections.addAll(all, others);
        return loopbackSend("", all);
    }

    @NotNull
    public MessageCreateAction sendMessageEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return loopbackSend("", new java.util.ArrayList<>(embeds));
    }

    @NotNull
    public MessageCreateAction sendMessageFormat(@NotNull String format, @NotNull Object... args) {
        return loopbackSend(String.format(format, args), Collections.emptyList());
    }

    private MessageCreateAction loopbackSend(String content, List<MessageEmbed> embeds) {
        FakeJDA jda = getFakeGuild().getFakeJDA();
        return new FakeMessageCreateAction(jda.asProxy(), () -> {
            User author = jda.getFakeSelfUser().asProxy();
            FakeMessage msg = new FakeMessage(jda, this, author, content, embeds);
            history.addLast(msg);
            jda.dispatchMessageReceived(msg);
            return msg.asProxy();
        }).typedMCA();
    }
}
