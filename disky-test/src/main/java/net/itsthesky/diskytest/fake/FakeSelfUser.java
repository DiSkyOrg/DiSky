package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.internal.entities.SelfUserImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Minimal stateful fake of the bot's own {@link SelfUser}.
 */
public class FakeSelfUser extends FakeEntity<SelfUser> {

    private final long id;
    private final String name;
    private final FakeJDA jda;

    public FakeSelfUser(FakeJDA jda, String name) {
        super(SelfUser.class, allInterfacesOf(SelfUserImpl.class));
        this.jda = jda;
        this.id = FakeEntity.nextSnowflake();
        this.name = name;
    }

    public long getIdLong() { return id; }
    public String getId() { return Long.toUnsignedString(id); }

    @NotNull public String getName() { return name; }
    @NotNull public String getEffectiveName() { return name; }
    @NotNull public String getDiscriminator() { return "0000"; }
    @NotNull public String getAsMention() { return "<@" + id + ">"; }

    public boolean isBot() { return true; }
    public boolean isSystem() { return false; }

    @NotNull public net.dv8tion.jda.api.JDA getJDA() { return jda.asProxy(); }
}
