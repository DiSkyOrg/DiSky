package net.itsthesky.disky.elements.properties.users;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
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

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.User;
import net.itsthesky.disky.api.generator.SeeAlso;
import org.jetbrains.annotations.Nullable;

@Name("User Tag Icon/Badge URL")
@Description({
        "Get the Icon Tag URL of the primary guild of a user.",
        "May be null if the user has no primary guild/no selected tag"
})
@Examples({
        "set {_image} to user tag icon of event-user"
})
@Since("4.27.0")
@SeeAlso({UserTag.class})
public class UserTagIconUrl extends SimplePropertyExpression<User, String> {

    static {
        register(
                UserTagIconUrl.class,
                String.class,
                "user [primary guild] tag icon [url]",
                "user"
        );
    }

    @Override
    public @Nullable String convert(User user) {
        return user.getPrimaryGuild() == null ? null : user.getPrimaryGuild().getBadgeUrl();
    }

    @Override
    protected String getPropertyName() {
        return "user tag icon url";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

}
