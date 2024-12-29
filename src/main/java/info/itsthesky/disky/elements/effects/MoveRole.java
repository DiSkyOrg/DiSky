package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
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
				"move [the] [discord] role %role% under [the] [discord] %role%",
				"move [the] [discord] role %role% up [%-number% time[s]]",
				"move [the] [discord] role %role% down [%-number% time[s]]"
		);
	}

	public enum MoveType {
		ABOVE, UNDER,
		UP, DOWN
	}

	private Expression<Role> exprTarget;

	private Expression<Role> exprRole;
	private Expression<Number> exprMod;

	private MoveType moveType;
	private Node node;

	@Override
	public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);
		node = getParser().getNode();

		exprTarget = (Expression<Role>) expressions[0];
		moveType = MoveType.values()[i];
		if (moveType == MoveType.ABOVE || moveType == MoveType.UNDER)
			exprRole = (Expression<Role>) expressions[1];
		else
			exprMod = (Expression<Number>) expressions[1];

		return true;
	}

	@Override
	public void execute(@NotNull Event e) {
		final Role target = parseSingle(exprTarget, e);
		final @Nullable Role role = parseSingle(exprRole, e);
		final Number mod = parseSingle(exprMod, e, 1);
		if (target == null) {
			DiSkyRuntimeHandler.exprNotSet(node, exprTarget);
			return;
		}

		if (role == null && (moveType == MoveType.ABOVE || moveType == MoveType.UNDER)) {
			DiSkyRuntimeHandler.exprNotSet(node, exprRole);
			return;
		}

		if (target.getGuild().getIdLong() != role.getGuild().getIdLong()) {
			DiSkyRuntimeHandler.error(new IllegalArgumentException("The specified roles are not in the same guild! (first is from '"+target.getGuild().getName()+"' and the second is from '"+role.getGuild().getName()+"')"), node);
			return;
		}

		if (mod.intValue() < 1) {
			DiSkyRuntimeHandler.error(new IllegalArgumentException("The modifier must be at least 1! Got: " + mod), node);
			return;
		}

		RoleOrderAction action = target.getGuild().modifyRolePositions();
        action = switch (moveType) {
            case ABOVE -> action.moveAbove(role);
            case UNDER -> action.moveBelow(role);
            case UP -> action.moveUp(mod.intValue());
            case DOWN -> action.moveDown(mod.intValue());
        };

		try {
			action.complete();
		} catch (Exception ex) {
			DiSkyRuntimeHandler.error(ex, node);
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		if (moveType == MoveType.ABOVE || moveType == MoveType.UNDER)
			return "move role " + exprTarget.toString(e, debug) + " " + moveType.name().toLowerCase() + " " + exprRole.toString(e, debug);
		return "move role " + exprTarget.toString(e, debug) + " " + moveType.name().toLowerCase() + " " + exprMod.toString(e, debug);
	}
}
