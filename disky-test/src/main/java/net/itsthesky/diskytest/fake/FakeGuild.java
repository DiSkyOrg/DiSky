package net.itsthesky.diskytest.fake;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.internal.entities.GuildImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal stateful fake of a {@link Guild}. Owns members, roles and text channels.
 */
public class FakeGuild extends FakeEntity<Guild> {

    private final FakeJDA jda;
    private final long id;
    private final String name;
    private final FakeMember selfMember;
    private final Map<Long, FakeMember> members = new LinkedHashMap<>();
    private final Map<Long, FakeRole> roles = new LinkedHashMap<>();
    private final Map<Long, FakeRichCustomEmoji> emotes = new LinkedHashMap<>();
    private final Map<Long, FakeTextChannel> textChannels = new LinkedHashMap<>();

    public FakeGuild(FakeJDA jda, String name, FakeMember selfMember) {
        super(Guild.class, allInterfacesOf(GuildImpl.class));
        this.jda = jda;
        this.id = nextSnowflake();
        this.name = name;
        this.selfMember = selfMember;
        members.put(selfMember.getIdLong(), selfMember);
    }

    public FakeJDA getFakeJDA() { return jda; }

    public void addFakeMember(FakeMember member) { members.put(member.getIdLong(), member); }
    public void addFakeRole(FakeRole role) { roles.put(role.getIdLong(), role); }
    public void addFakeTextChannel(FakeTextChannel channel) { textChannels.put(channel.getIdLong(), channel); }
    public void addFakeEmoji(FakeRichCustomEmoji emoji) { emotes.put(emoji.getIdLong(), emoji); }

    public long getIdLong() { return id; }
    public String getId() { return Long.toUnsignedString(id); }
    @NotNull public String getName() { return name; }

    @NotNull public net.dv8tion.jda.api.JDA getJDA() { return jda.asProxy(); }

    //region Members

    @NotNull
    public Member getSelfMember() { return selfMember.asProxy(); }

    @Nullable
    public Member getMemberById(long memberId) {
        FakeMember m = members.get(memberId);
        return m == null ? null : m.asProxy();
    }

    @Nullable
    public Member getMember(@NotNull net.dv8tion.jda.api.entities.UserSnowflake user) {
        return getMemberById(user.getIdLong());
    }

    @NotNull
    public List<Member> getMembers() {
        if (members.isEmpty()) return Collections.emptyList();
        List<Member> out = new ArrayList<>(members.size());
        for (FakeMember m : members.values()) out.add(m.asProxy());
        return out;
    }

    //endregion

    @NotNull
    public List<TextChannel> getTextChannels() {
        if (textChannels.isEmpty()) return Collections.emptyList();
        List<TextChannel> out = new ArrayList<>(textChannels.size());
        for (FakeTextChannel c : textChannels.values()) out.add(c.asProxy());
        return out;
    }

    @Nullable
    public TextChannel getTextChannelById(long channelId) {
        FakeTextChannel c = textChannels.get(channelId);
        return c == null ? null : c.asProxy();
    }

    @NotNull
    public List<Role> getRoles() {
        if (roles.isEmpty()) return Collections.emptyList();
        List<Role> out = new ArrayList<>(roles.size());
        for (FakeRole r : roles.values()) out.add(r.asProxy());
        return out;
    }

    @Nullable
    public Role getRoleById(long roleId) {
        FakeRole r = roles.get(roleId);
        return r == null ? null : r.asProxy();
    }

    //region Emojis

    public @Nullable RichCustomEmoji getEmojiById(@Nonnull String id) {
        return getEmojiById(Long.parseUnsignedLong(id));
    }

    public @Nullable RichCustomEmoji getEmojiById(long id) {
        FakeRichCustomEmoji e = emotes.get(id);
        return e == null ? null : e.asProxy();
    }

    public @NotNull List<RichCustomEmoji> getEmojis() {
        if (emotes.isEmpty()) return Collections.emptyList();
        List<RichCustomEmoji> out = new ArrayList<>(emotes.size());
        for (FakeRichCustomEmoji e : emotes.values()) out.add(e.asProxy());
        return out;
    }

    public @NotNull List<RichCustomEmoji> getEmojisByName(@Nonnull String name, boolean ignoreCase) {
        List<RichCustomEmoji> out = new ArrayList<>();
        for (FakeRichCustomEmoji e : emotes.values()) {
            if (e.getName().equalsIgnoreCase(name))
                out.add(e.asProxy());
        }
        return out;
    }

    //endregion
}
