package info.itsthesky.disky.elements.properties.threads;

import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Thread Channel Parent")
public class ThreadParent extends SimplePropertyExpression<ThreadChannel, GuildMessageChannel> {

	static {
		register(
				ThreadParent.class,
				GuildMessageChannel.class,
				"[the] thread parent [channel]",
				"threadchannel"
		);
	}

	@Override
	public @Nullable GuildMessageChannel convert(ThreadChannel threadChannel) {
		return threadChannel.getParentChannel().asTextChannel();
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "thread parent";
	}

	@Override
	public @NotNull Class<? extends GuildMessageChannel> getReturnType() {
		return GuildMessageChannel.class;
	}
}
