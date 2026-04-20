package net.itsthesky.diskytest.fake;

/*
 * DiSky
 * Copyright (C) 2026 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.internal.entities.emoji.RichCustomEmojiImpl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FakeRichCustomEmoji extends FakeEntity<RichCustomEmoji> {

    private final long id;
    private final Set<Role> roles;
    private final FakeJDA api;

    private final FakeGuild guild;
    private final boolean managed = false;
    private final boolean available = true;
    private final boolean animated;
    private final String name;
    private final FakeUser owner;

    public FakeRichCustomEmoji(FakeGuild guild, FakeUser owner, String name, long id, boolean animated) {
        super(RichCustomEmoji.class, allInterfacesOf(RichCustomEmojiImpl.class));

        this.guild = guild;
        this.owner = owner;
        this.id = id;
        this.name = name;
        this.animated = animated;
        this.roles = Collections.emptySet();
        this.api = guild.getFakeJDA();

        guild.addFakeEmoji(this);
    }

    @Nonnull
    public Guild getGuild() {
        return guild.asProxy();
    }

    @Nonnull
    public List<Role> getRoles() {
        return List.copyOf(roles);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public boolean isManaged() {
        return managed;
    }

    public boolean isAvailable() {
        return available;
    }

    public long getIdLong() {
        return id;
    }

    public User getOwner() {
        return owner.asProxy();
    }

    public JDA getJDA() {
        return api.asProxy();
    }

    @Nonnull
    public UnicodeEmoji asUnicode() {
        throw new IllegalStateException("Cannot convert CustomEmoji to UnicodeEmoji!");
    }

    @Nonnull
    public CustomEmoji asCustom() {
        return asProxy();
    }

    @Nonnull
    public RichCustomEmoji asRich() {
        return asProxy();
    }

    public boolean isAnimated() {
        return animated;
    }

    public Emoji.Type getType() {
        return Emoji.Type.CUSTOM;
    }
}
