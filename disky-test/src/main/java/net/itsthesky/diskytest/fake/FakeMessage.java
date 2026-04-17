package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.entities.messages.MessageSnapshot;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.internal.entities.MessageMentionsImpl;
import net.dv8tion.jda.internal.entities.ReceivedMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * Minimal stateful fake of a {@link Message}.
 *
 * <p>Holds id, content, author, channel and embeds. Everything else (attachments,
 * reactions, components, mentions...) throws via the proxy until needed.
 */
public class FakeMessage extends FakeEntity<Message> {

    private final FakeJDA jda;
    private final FakeTextChannel channel;
    private final User author;
    private final long id;
    private final String content;
    private final List<MessageEmbed> embeds;
    private final OffsetDateTime timeCreated;

    public FakeMessage(FakeJDA jda, FakeTextChannel channel, User author,
                       String content, List<MessageEmbed> embeds) {
        super(Message.class, allInterfacesOf(ReceivedMessage.class));
        this.jda = jda;
        this.channel = channel;
        this.author = author;
        this.id = nextSnowflake();
        this.content = content == null ? "" : content;
        this.embeds = embeds == null ? Collections.emptyList() : new ArrayList<>(embeds);
        this.timeCreated = OffsetDateTime.now();
    }

    public long getIdLong() { return id; }
    public String getId() { return Long.toUnsignedString(id); }

    @NotNull public String getContentRaw() { return content; }
    @NotNull public String getContentDisplay() { return content; }
    @NotNull public String getContentStripped() { return content; }

    public long getChannelIdLong() { return id; }
    public long getGuildIdLong() { return channel.getGuild().getIdLong(); }

    @NotNull public MessageType getType() { return MessageType.DEFAULT; }

    @NotNull public User getAuthor() { return author; }

    @Nullable
    public Member getMember() {
        Guild guild = channel.getGuild();
        return guild.getMember(author);
    }

    @NotNull public MessageChannel getChannel() { return channel.asProxy(); }

    /** Returns the channel as a union — implemented by the same proxy. */
    @NotNull public MessageChannelUnion getChannelUnion() {
        // The TextChannel proxy implements MessageChannel; cast through the union interface.
        // JDA's MessageChannelUnion is a marker interface extending MessageChannel.
        return (MessageChannelUnion) channel.asProxy();
    }

    @NotNull public Guild getGuild() { return channel.getGuild(); }
    public boolean isFromGuild() { return true; }
    @NotNull public ChannelType getChannelType() { return ChannelType.TEXT; }

    @NotNull public List<MessageEmbed> getEmbeds() { return Collections.unmodifiableList(embeds); }

    @NotNull public net.dv8tion.jda.api.JDA getJDA() { return jda.asProxy(); }
    @NotNull public OffsetDateTime getTimeCreated() { return timeCreated; }

     public boolean isWebhookMessage() { return false; }
     public boolean isEdited() { return false; }
     public boolean isPinned() { return false; }
     public boolean isTTS() { return false; }
     public boolean isMentioned(@NotNull net.dv8tion.jda.api.entities.IMentionable mentionable) { return false; }

     @NotNull public String getJumpUrl() {
         return "https://discord.com/channels/" + channel.getGuild().getId() + "/" + channel.getId() + "/" + id;
     }

     @Nullable public MessageReference getMessageReference() { return null; }
     public long getApplicationIdLong() { return 0; }
     @Nullable public String getNonce() { return null; }
     @Nullable public MessageActivity getActivity() { return null; }
     @Nullable public MessagePoll getPoll() { return null; }
     @Nullable public OffsetDateTime getTimeEdited() { return null; }

     @NotNull public Mentions getMentions() { return null; }
     @NotNull public List<Role> getMentionedRoles() { return Collections.emptyList(); }
     @NotNull public List<GuildChannel> getMentionedChannels() { return Collections.emptyList(); }

     @NotNull public List<MessageReaction> getReactions() { return Collections.emptyList(); }
     @NotNull public List<Message.Attachment> getAttachments() { return Collections.emptyList(); }
     @NotNull public List<RichCustomEmoji> getStickers() { return Collections.emptyList(); }
     @NotNull public List<ActionRow> getComponents() { return Collections.emptyList(); }
     @NotNull public List<MessageSnapshot> getMessageSnapshots() { return Collections.emptyList(); }

     @NotNull public EnumSet<Message.MessageFlag> getFlags() { return EnumSet.noneOf(Message.MessageFlag.class); }
     public int getFlagsRaw() { return 0; }

     @Nullable public Interaction getInteraction() { return null; }
     @Nullable public Message.InteractionMetadata getInteractionMetadata() { return null; }
     @Nullable public ThreadChannel getStartedThread() { return null; }
 }
