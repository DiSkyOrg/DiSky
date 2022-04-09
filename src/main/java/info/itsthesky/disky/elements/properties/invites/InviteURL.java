package info.itsthesky.disky.elements.properties.invites;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Invite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Invite URL")
@Description("Represent the unique invite code used in the Discord URL.")
@Examples("reply with invite url of event-invite")
public class InviteURL extends InviteProperty<String> {

    static {
        register(
                InviteCode.class,
                String.class,
                "url"
        );
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @Nullable String convert(Invite invite) {
        return invite.getUrl();
    }
}
