package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base for all fake guild channels.
 *
 * <p>Holds the fields that every channel type shares: snowflake ID, name, parent guild.
 * Subclasses only need to supply the primary JDA interface and the additional proxy
 * interfaces (via {@link #allInterfacesOf}), then implement channel-type-specific
 * behaviour on top.
 *
 * <p>The {@link FakeEntity} invocation handler already walks the full Java class
 * hierarchy when resolving a proxied method, so methods declared here are transparently
 * available through the subclass proxy without any extra wiring.
 *
 * @param <I> the JDA channel interface this fake implements (e.g. {@code TextChannel})
 */
public abstract class FakeChannel<I> extends FakeEntity<I> {

    private final FakeGuild guild;
    private final long id;
    private final String name;

    protected FakeChannel(Class<I> interfaceClass, FakeGuild guild, String name,
                          Class<?>... additionalInterfaces) {
        super(interfaceClass, additionalInterfaces);
        this.guild = guild;
        this.id = nextSnowflake();
        this.name = name;
    }

    // ===== Accessors for subclasses =====

    public FakeGuild getFakeGuild() { return guild; }

    // ===== JDA channel interface methods =====

    public long getIdLong() { return id; }

    @NotNull
    public String getId() { return Long.toUnsignedString(id); }

    @NotNull
    public String getName() { return name; }

    @NotNull
    public abstract ChannelType getType();

    @NotNull
    public Guild getGuild() { return guild.asProxy(); }

    @NotNull
    public JDA getJDA() { return guild.getFakeJDA().asProxy(); }

    @NotNull
    public String getAsMention() { return "<#" + id + ">"; }

    /**
     * Returns this channel cast to {@link MessageChannelUnion}.
     * Works because the proxy declares all interfaces of the concrete JDA impl.
     */
    @NotNull
    public MessageChannelUnion getChannel() { return (MessageChannelUnion) asProxy(); }

    public boolean canTalk() { return true; }

    public boolean canTalk(@NotNull Member member) { return true; }
}
