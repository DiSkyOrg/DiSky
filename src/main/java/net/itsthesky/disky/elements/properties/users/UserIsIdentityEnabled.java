package net.itsthesky.disky.elements.properties.users;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.User;
import net.itsthesky.disky.api.generator.SeeAlso;

@Name("User is Showing Server Tag")
@Description({"Check whether the provided user is displaying their primary guild's server tag.",
        "This will be false if the user has no primary guild, or has the tag hidden."})
@Examples({"event-user is showing their server tag",
        "event-member is not displaying the primary guild tag"})
@Since("4.28.0")
@SeeAlso(User.class)
public class UserIsIdentityEnabled extends PropertyCondition<User> {

    static {
        register(
                UserIsIdentityEnabled.class,
                PropertyType.BE,
                "(showing|displaying) [the[ir]] [primary guild] [server] tag",
                "users"
        );
    }

    @Override
    public boolean check(User user) {
        final User.PrimaryGuild primaryGuild = user.getPrimaryGuild();
        return primaryGuild != null && primaryGuild.isIdentityEnabled();
    }

    @Override
    protected String getPropertyName() {
        return "showing server tag";
    }
}