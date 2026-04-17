package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.entities.RoleImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Minimal stateful fake of a {@link Role} attached to a {@link FakeGuild}.
 */
public class FakeRole extends FakeEntity<Role> {

    private final long id;
    private final String name;
    private final FakeGuild guild;

    public FakeRole(FakeGuild guild, String name) {
        super(Role.class, allInterfacesOf(RoleImpl.class));
        this.guild = guild;
        this.id = nextSnowflake();
        this.name = name;
    }

    public long getIdLong() { return id; }
    public String getId() { return Long.toUnsignedString(id); }
    @NotNull public String getName() { return name; }
    @NotNull public String getAsMention() { return "<@&" + id + ">"; }

    @NotNull public Guild getGuild() { return guild.asProxy(); }
    @NotNull public net.dv8tion.jda.api.JDA getJDA() { return guild.getFakeJDA().asProxy(); }
}
