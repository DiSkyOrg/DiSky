package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.WaiterEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.Debug;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffUpdateCommand extends WaiterEffect {

	static {
		Skript.registerEffect(
				EffUpdateCommand.class,
				"(update|register) [the] [command[s]] %slashcommands% [(1¦globally|2¦locally)] in [the] [(bot|guild)] %bot/guild%"
		);
	}

	private boolean isGlobal;
	private Expression<SlashCommandData> exprCommands;
	private Expression<Object> exprEntity;

	@Override
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		exprCommands = (Expression<SlashCommandData>) expressions[0];
		exprEntity = (Expression<Object>) expressions[1];
		isGlobal = (parseResult.mark & 1) != 0;
		return true;
	}

	@Override
	public void runEffect(Event e) {
		final SlashCommandData[] commands = parseList(exprCommands, e, new SlashCommandData[0]);
		final Object entity = parseSingle(exprEntity, e, null);
		if (commands.length == 0) {
			Debug.debug(this, Debug.Type.EMPTY_LIST, "No commands found.");
			restart();
			return;
		}
		if (anyNull(this, entity)) {
			restart();
			return;
		}

		if (isGlobal && !(entity instanceof Bot)) {
			Debug.debug(this, Debug.Type.INCOMPATIBLE_TYPE, "The entity must be a bot to update commands globally!");
			restart();
			return;
		}

		if (!isGlobal && !(entity instanceof Guild)) {
			Debug.debug(this, Debug.Type.INCOMPATIBLE_TYPE, "The entity must be a guild to update commands locally!");
			restart();
			return;
		}

		final CommandListUpdateAction updateAction;
		if (isGlobal)
			updateAction = ((Bot) entity).getInstance().updateCommands();
		else
			updateAction = ((Guild) entity).updateCommands();

		updateAction.addCommands(commands)
				.queue(this::restart, ex -> {
					restart();
					DiSky.getErrorHandler().exception(e, ex);
				});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "update commands " + exprCommands.toString(e, debug) + " in " +
				exprEntity.toString(e, debug);
	}

}
