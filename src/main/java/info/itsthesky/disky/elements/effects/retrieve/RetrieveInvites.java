package info.itsthesky.disky.elements.effects.retrieve;

import info.itsthesky.disky.api.skript.BaseMultipleRetrieveEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RetrieveInvites extends BaseMultipleRetrieveEffect<List<Invite>, Guild> {

    static {
        register(
                RetrieveInvites.class,
                "invites",
                "guild"
        );
    }

    @Override
    protected RestAction<List<Invite>> retrieve(@NotNull Guild entity) {
        return entity.retrieveInvites();
    }

}
