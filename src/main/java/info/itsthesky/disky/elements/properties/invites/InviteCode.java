package info.itsthesky.disky.elements.properties.invites;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Invite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Invite Code")
@Description("Represent the plain Discord URL that people have to click on in order to join the invite's guild.")
@Examples("reply with invite code of event-invite")
public class InviteCode extends InviteProperty<String> {

    static {
        register(
                InviteCode.class,
                String.class,
                "code"
        );
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @Nullable String convert(Invite invite) {
        return invite.getCode();
    }
}
