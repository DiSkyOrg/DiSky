package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Minimal stateful fake of a Discord {@link User}.
 *
 * <p>Only the most commonly accessed identity methods are implemented; everything
 * else throws {@link UnsupportedOperationException} via the proxy.
 */
public class FakeUser extends FakeEntity<User> {

    private final long id;
    private final String name;
    private final String discriminator;
    private final boolean bot;
    private final FakeJDA jda;

    public FakeUser(FakeJDA jda, String name, boolean bot) {
        super(User.class, allInterfacesOf(UserImpl.class));
        this.jda = jda;
        this.id = nextSnowflake();
        this.name = name;
        this.discriminator = "0000";
        this.bot = bot;
    }

    public long getIdLong() { return id; }
    public String getId() { return Long.toUnsignedString(id); }

    @NotNull
    public String getName() { return name; }

    @NotNull
    public String getEffectiveName() { return name; }

    @NotNull
    public String getDiscriminator() { return discriminator; }

    @NotNull
    public String getAsMention() { return "<@" + id + ">"; }

    public boolean isBot() { return bot; }

    public boolean isSystem() { return false; }

    @NotNull
    public net.dv8tion.jda.api.JDA getJDA() { return jda.asProxy(); }
}
