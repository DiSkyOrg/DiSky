package net.itsthesky.disky.elements.properties.invites;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Invite Inviter")
@Description("Represent the user who created the invite.")
@Examples("reply with mention tag of invite inviter of event-invite")
public class InviteInviter extends InviteProperty<User> {

    static {
        register(
                InviteInviter.class,
                User.class,
                "(inviter|author)"
        );
    }

    @Override
    public @NotNull Class<? extends User> getReturnType() {
        return User.class;
    }

    @Override
    public @Nullable User convert(Invite invite) {
        return invite.getInviter();
    }
}
