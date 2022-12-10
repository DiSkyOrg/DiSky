package info.itsthesky.disky.elements.properties.threads;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

@Name("Thread Is Locked")
@Description("Check if a thread is locked or not.")
@Since("4.8.0")
public class ThreadIsLocked extends PropertyCondition<ThreadChannel> {

	static {
		register(
				ThreadIsLocked.class,
				PropertyType.BE,
				"[a] locked [thread]",
				"threadchannel"
		);
	}

	@Override
	public boolean check(ThreadChannel threadChannel) {
		return threadChannel.isLocked();
	}

	@Override
	protected String getPropertyName() {
		return "locked";
	}
}
