package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.BaseMultipleRetrieveEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Name("Retrieve Threads")
@Description({"Retrieve every threads (and cache them) from a specific guild.",
        "This effect will only get back the ACTIVE thread, and will pass on the archived ones."})
public class RetrieveThreads extends BaseMultipleRetrieveEffect<List<ThreadChannel>, Guild> {

    static {
        register(
                RetrieveThreads.class,
                "thread[s]",
                "guild"
        );
    }

    @Override
    protected RestAction<List<ThreadChannel>> retrieve(@NotNull Guild entity) {
        return entity.retrieveActiveThreads();
    }

}
