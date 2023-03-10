package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.BaseMultipleRetrieveEffect;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Name("Retrieve Thread Members")
@Description({"Retrieve every members (and cache them) from a specific thread."})
public class RetrieveThreadMembers extends BaseMultipleRetrieveEffect<List<ThreadMember>, ThreadChannel> {

    static {
        register(
                RetrieveThreadMembers.class,
                "thread members",
                "threadchannel"
        );
    }

    @Override
    protected RestAction<List<ThreadMember>> retrieve(@NotNull ThreadChannel entity) {
        return entity.retrieveThreadMembers();
    }

    @Override
    protected List<?> convert(List<ThreadMember> original) {
        return original.stream().map(ThreadMember::getMember).collect(Collectors.toList());
    }
}
