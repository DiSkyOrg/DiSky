package net.itsthesky.disky.elements.sections.automod;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class NewRuleResponse extends SimpleExpression<AutoModResponse> {

	static {
		DiSkyRegistry.registerExpression(
				NewRuleResponse.class,
				AutoModResponse.class,
				ExpressionType.COMBINED,
				"block [member] interaction",
				"block message[s] [with [the] [reason] %-string%]",
				"send [an] alert (in|at|to) [the] [channel] %channel/textchannel%",
				"timeout [the] member [for] %timespan%"
		);
	}

	private int pattern;

	private Expression<String> exprReason; // 1
	private Expression<Channel> exprChannel; // 2
	private Expression<Timespan> exprTime; // 3

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		pattern = matchedPattern;

		switch (pattern) {
			case 1:
				exprReason = (Expression<String>) exprs[0];
				break;
			case 2:
				exprChannel = (Expression<Channel>) exprs[0];
				break;
			case 3:
				exprTime = (Expression<Timespan>) exprs[0];
				break;
		}

		return true;
	}

	@Override
	protected AutoModResponse @NotNull [] get(@NotNull Event e) {

		switch (pattern) {
			case 0:
				return new AutoModResponse[] {AutoModResponse.blockMemberInteraction()};
			case 1:
				return new AutoModResponse[] {AutoModResponse.blockMessage(EasyElement.parseSingle(exprReason, e))};
			case 2:
				final Channel channel = EasyElement.parseSingle(exprChannel, e);
				if (!channel.getType().isGuild() || !channel.getType().isMessage()) {
					Skript.error("The channel '"+channel.getName()+"' is not a guild & text channel, so it cannot be used in an automod rule!");
					return new AutoModResponse[0];
				}
				return new AutoModResponse[] {AutoModResponse.sendAlert((GuildMessageChannel) channel)};
			case 3:
				final Timespan timespan = EasyElement.parseSingle(exprTime, e);
				if (timespan.getAs(Timespan.TimePeriod.TICK) <= 0) {
					Skript.error("The timespan '"+timespan.toString()+"' is not valid, it must be greater than 0!");
					return new AutoModResponse[0];
				}
				return new AutoModResponse[] {AutoModResponse.timeoutMember(Duration.ofMillis((timespan.getAs(Timespan.TimePeriod.MILLISECOND))))};
		}

		return new AutoModResponse[0];
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends AutoModResponse> getReturnType() {
		return AutoModResponse.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		switch (pattern) {
			case 0:
				return "block member interaction";
			case 1:
				return "block message with reason " + exprReason.toString(e, debug);
			case 2:
				return "send alert in channel " + exprChannel.toString(e, debug);
			case 3:
				return "timeout member for " + exprTime.toString(e, debug);
			default:
				throw new IllegalStateException("Unexpected value: " + pattern);
		}
	}

}
