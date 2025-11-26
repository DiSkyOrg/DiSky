package net.itsthesky.disky.elements.properties.threads;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

@Name("Thread Is Public")
@Description("Check if a thread is public or not.")
@Examples({"if event-threadchannel is a public thread:",
        "\treply with \"This is a public thread!\""})
@Since("4.8.0")
public class ThreadIsPublic extends PropertyCondition<ThreadChannel> {

	static {
		register(
				ThreadIsPublic.class,
				PropertyType.BE,
				"[a] public [thread]",
				"threadchannel"
		);
	}

	@Override
	public boolean check(ThreadChannel threadChannel) {
		return threadChannel.isPublic();
	}

	@Override
	protected String getPropertyName() {
		return "public";
	}
}
