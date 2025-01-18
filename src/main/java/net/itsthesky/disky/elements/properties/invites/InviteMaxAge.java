package net.itsthesky.disky.elements.properties.invites;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Invite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Invite Max Age")
@Description("Represent the max age time this invite can be used.")
@Examples("reply with invite max age of event-invite")
public class InviteMaxAge extends InviteProperty<Number> {

    static {
        register(
                InviteMaxAge.class,
                Number.class,
                "max age[s]"
        );
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @Nullable Number convert(Invite invite) {
        return invite.getMaxAge();
    }
}
