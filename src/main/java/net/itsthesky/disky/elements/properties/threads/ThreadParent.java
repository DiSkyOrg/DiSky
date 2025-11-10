package net.itsthesky.disky.elements.properties.threads;

import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Thread Channel Parent")
public class ThreadParent extends SimplePropertyExpression<ThreadChannel, GuildChannel> {

	static {
		register(
				ThreadParent.class,
				GuildChannel.class,
				"thread parent [channel]",
				"threadchannel"
		);
	}

	@Override
	public @Nullable GuildChannel convert(ThreadChannel threadChannel) {
		return threadChannel.getParentChannel();
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "thread parent";
	}

	@Override
	public @NotNull Class<? extends GuildChannel> getReturnType() {
		return GuildChannel.class;
	}
}
