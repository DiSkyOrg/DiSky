package net.itsthesky.disky.elements.properties.invites;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Invite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Invite Max Uses")
@Description("Represent the max amount of times this invite can be used.")
@Examples("reply with invite max use of event-invite")
public class InviteMaxUses extends InviteProperty<Number> {

    static {
        register(
                InviteMaxUses.class,
                Number.class,
                "max use[s]"
        );
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @Nullable Number convert(Invite invite) {
        return invite.getMaxUses();
    }
}
