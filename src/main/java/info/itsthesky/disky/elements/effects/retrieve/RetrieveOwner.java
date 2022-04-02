package info.itsthesky.disky.elements.effects.retrieve;

import info.itsthesky.disky.api.skript.BaseRetrieveEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

public class RetrieveOwner extends BaseRetrieveEffect<Member, Guild> {

    static {
        register(
                RetrieveOwner.class,
                "owner",
                "guild",
                false, true
        );
    }

    @Override
    protected boolean requireID() {
        return false;
    }

    @Override
    protected RestAction<Member> retrieve(@NotNull String input, @NotNull Guild entity) {
        return entity.retrieveOwner();
    }

}
