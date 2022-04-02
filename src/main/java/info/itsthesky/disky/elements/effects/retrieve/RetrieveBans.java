package info.itsthesky.disky.elements.effects.retrieve;

import info.itsthesky.disky.api.skript.BaseMultipleRetrieveEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RetrieveBans extends BaseMultipleRetrieveEffect<List<Guild.Ban>, Guild> {

    static {
        register(
                RetrieveBans.class,
                "bans",
                "guild"
        );
    }

    @Override
    protected RestAction<List<Guild.Ban>> retrieve(@NotNull Guild entity) {
        return entity.retrieveBanList();
    }

}
