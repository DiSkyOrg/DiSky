package info.itsthesky.disky.elements.effects.retrieve;

import info.itsthesky.disky.api.skript.BaseRetrieveEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

public class RetrieveInvite extends BaseRetrieveEffect<Invite, Guild> {

    static {
        register(
                RetrieveInvite.class,
                "invite",
                "guild"
        );
    }

    @Override
    protected RestAction<Invite> retrieve(@NotNull String input, @NotNull Guild entity) {
        return Invite.resolve(entity.getJDA(), input);
    }

}
