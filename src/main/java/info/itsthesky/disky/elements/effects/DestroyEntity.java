package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.WaiterEffect;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Destroy Discord Entity")
@Description("Destroy on Discord the wanted entity.")
@Examples({"destroy event-channel",
"destroy event-message"})
public class DestroyEntity extends WaiterEffect {

	static {
		Skript.registerEffect(
				DestroyEntity.class,
				"destroy %guild/message/role/channel/emote%"
		);
	}

	private Expression<Object> exprEntity;

	@Override
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		exprEntity = (Expression<Object>) expressions[0];
		return true;
	}

	@Override
	public void runEffect(Event e) {
		final Object entity = parseSingle(exprEntity, e, null);
		if (anyNull(entity)) {
			restart();
			return;
		}
		final RestAction<Void> action;
		if (entity instanceof Guild)
			action = ((Guild) entity).delete();
		else if (entity instanceof Role)
			action = ((Role) entity).delete();
		else if (entity instanceof Message)
			action = ((Message) entity).delete();
		else if (entity instanceof Channel)
			action = ((Channel) entity).delete();
		else if (entity instanceof info.itsthesky.disky.api.emojis.Emote && ((info.itsthesky.disky.api.emojis.Emote) entity).isEmote())
			action = ((info.itsthesky.disky.api.emojis.Emote) entity).getEmote().delete();
		else
			action = null;
		if (anyNull(action)) {
			restart();
			return;
		}
		action.queue(v -> restart(), ex -> {
			restart();
			DiSky.getErrorHandler().exception(e, ex);
		});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "destroy " + exprEntity.toString(e, debug);
	}
}
