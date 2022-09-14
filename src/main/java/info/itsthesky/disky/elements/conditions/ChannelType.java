package info.itsthesky.disky.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelType extends Condition {

	static {
		Skript.registerCondition(ChannelType.class,
				"%channel% is of [the] %channeltype% type",
				"%channel% is of [the] type %channeltype%"
		);
	}

	private Expression<Channel> exprChannel;
	private Expression<net.dv8tion.jda.api.entities.channel.ChannelType> exprType;

	@Override
	public boolean check(@NotNull Event e) {
		final Channel channel = EasyElement.parseSingle(exprChannel, e, null);
		final net.dv8tion.jda.api.entities.channel.ChannelType type = EasyElement.parseSingle(exprType, e, null);
		if (EasyElement.anyNull(channel, type))
			return false;
		return channel.getType().equals(type);
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return exprChannel.toString(e, debug) + " is of " + exprType.toString(e, debug) + " type";
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprChannel = (Expression<Channel>) exprs[0];
		exprType = (Expression<net.dv8tion.jda.api.entities.channel.ChannelType>) exprs[1];
		return true;
	}
}
