package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import info.itsthesky.disky.api.skript.BaseMultipleRetrieveEffect;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Name("Retrieve Interested Members")
@Description("Retrieve all members who are interested in a scheduled event.")
@Since("4.8.0")
public class RetrieveInterestedMembers extends BaseMultipleRetrieveEffect<List<Member>, ScheduledEvent> {

	static {
		register(
				RetrieveEmotes.class,
				"interested members",
				"scheduledevent"
		);
	}

	@Override
	protected RestAction<List<Member>> retrieve(@NotNull ScheduledEvent entity) {
		return entity.retrieveInterestedMembers();
	}
}
