package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static info.itsthesky.disky.api.skript.EasyElement.*;

@Name("Move Role Above/Under Role")
@Description({"Move a specific role above or under another role within the same guild.",
"The indexes will be updated automatically."})
@Examples("move role {_role} above role with id \"000\"")
public class MoveRole extends AsyncEffect {

	static {
		Skript.registerEffect(
				MoveRole.class,
				"move [the] [discord] role %role% above [the] [discord] %role%",
				"move [the] [discord] role %role% under [the] [discord] %role%"
		);
	}

	private Expression<Role> exprTarget;
	private Expression<Role> exprRole;
	private boolean isAbove;

	@Override
	public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);

		exprTarget = (Expression<Role>) expressions[0];
		exprRole = (Expression<Role>) expressions[1];
		isAbove = i == 0;

		return true;
	}

	@Override
	public void execute(@NotNull Event e) {
		final Role target = parseSingle(exprTarget, e);
		final Role role = parseSingle(exprRole, e);
		if (target == null || role == null) return;

		if (target.getGuild().getIdLong() != role.getGuild().getIdLong()) {
			Skript.error("The specified roles are not in the same guild!");
			return;
		}

		RoleOrderAction action = target.getGuild().modifyRolePositions();
		if (isAbove)
			action = action.moveAbove(role);
		else
			action = action.moveBelow(role);

		try {
			action.complete();
		} catch (Exception ex) {
			DiSky.getErrorHandler().exception(e, ex);
			return;
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "move role " + exprTarget.toString(e, debug) + " " + (isAbove ? "above" : "under") + " role " + exprRole.toString(e, debug);
	}
}
