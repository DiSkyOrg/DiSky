package net.itsthesky.disky.elements.properties.threads;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Thread Message Count")
@Description("Get the approximate message count of a thread channel.")
@Examples("set {_count} to message count of event-thread")
@Since("4.26.0")
public class ThreadMessageCount extends SimplePropertyExpression<ThreadChannel, Number> {

	static {
		register(
				ThreadMessageCount.class,
				Number.class,
				"[the] [thread] message count",
				"threadchannel"
		);
	}

	@Override
	public @Nullable Number convert(ThreadChannel threadChannel) {
		return threadChannel.getMessageCount();
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "thread message count";
	}

	@Override
	public @NotNull Class<? extends Number> getReturnType() {
		return Number.class;
	}
}
