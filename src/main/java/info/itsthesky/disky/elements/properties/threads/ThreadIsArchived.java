package info.itsthesky.disky.elements.properties.threads;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

@Name("Thread Is Archived")
@Description("Check if a thread is archived or not.")
@Since("4.8.0")
public class ThreadIsArchived extends PropertyCondition<ThreadChannel> {

	static {
		register(
				ThreadIsArchived.class,
				PropertyType.BE,
				"[a] archived [thread]",
				"threadchannel"
		);
	}

	@Override
	public boolean check(ThreadChannel threadChannel) {
		return threadChannel.isArchived();
	}

	@Override
	protected String getPropertyName() {
		return "archived";
	}
}
