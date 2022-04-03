package info.itsthesky.disky.elements.properties.invites;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Invite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Invite Uses")
@Description("Represent the amount of times this invite has been used.")
@Examples("reply with invite uses of event-invite")
public class InviteUses extends InviteProperty<Number> {

    static {
        register(
                InviteUses.class,
                Number.class,
                "use[s]"
        );
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @Nullable Number convert(Invite invite) {
        return invite.getUses();
    }
}
