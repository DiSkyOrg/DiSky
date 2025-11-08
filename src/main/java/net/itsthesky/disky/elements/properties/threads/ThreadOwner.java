package net.itsthesky.disky.elements.properties.threads;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Thread Owner")
@Description("Get the owner (member) of a thread channel.")
@Examples("set {_owner} to thread owner of event-thread")
@Since("4.26.0")
public class ThreadOwner extends SimplePropertyExpression<ThreadChannel, Member> {

	static {
		register(
				ThreadOwner.class,
				Member.class,
				"[the] [thread] owner",
				"threadchannel"
		);
	}

	@Override
	public @Nullable Member convert(ThreadChannel threadChannel) {
		return threadChannel.getOwner();
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "thread owner";
	}

	@Override
	public @NotNull Class<? extends Member> getReturnType() {
		return Member.class;
	}
}
