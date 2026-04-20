package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.entities.MemberImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minimal stateful fake of a guild {@link Member}.
 */
public class FakeMember extends FakeEntity<Member> {

    private final FakeGuild guild;
    private final FakeUser user;
    private @Nullable String nickname;
    private final List<FakeRole> roles = new ArrayList<>();

    public FakeMember(FakeGuild guild, FakeUser user) {
        super(Member.class, allInterfacesOf(MemberImpl.class));
        this.guild = guild;
        this.user = user;

        if (guild != null)
            guild.addFakeMember(this);
    }

    public void setNickname(@Nullable String nickname) { this.nickname = nickname; }
    public void addRole(FakeRole role) { roles.add(role); }
    public List<FakeRole> getFakeRoles() { return roles; }

    public long getIdLong() { return user.getIdLong(); }
    public String getId() { return user.getId(); }

    @NotNull public Guild getGuild() { return guild.asProxy(); }
    @NotNull public User getUser() { return user.asProxy(); }
    @NotNull public net.dv8tion.jda.api.JDA getJDA() { return guild.getFakeJDA().asProxy(); }

    @NotNull public String getEffectiveName() { return nickname != null ? nickname : user.getName(); }
    @Nullable public String getNickname() { return nickname; }

    @NotNull public String getAsMention() { return "<@" + user.getIdLong() + ">"; }

    @NotNull
    public List<net.dv8tion.jda.api.entities.Role> getRoles() {
        if (roles.isEmpty()) return Collections.emptyList();
        List<net.dv8tion.jda.api.entities.Role> out = new ArrayList<>(roles.size());
        for (FakeRole r : roles) out.add(r.asProxy());
        return out;
    }
}
