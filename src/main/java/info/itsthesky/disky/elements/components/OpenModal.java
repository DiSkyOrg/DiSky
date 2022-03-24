package info.itsthesky.disky.elements.components;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.MessageEvent;
import info.itsthesky.disky.api.events.specific.ModalEvent;
import info.itsthesky.disky.api.skript.WaiterEffect;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenModal extends WaiterEffect {

	static {
		Skript.registerEffect(
				OpenModal.class,
				"(show|enable) [the] [modal] %modal% [to [the] [event[( |-)]]user]"
		);
	}

	private Expression<Modal.Builder> exprModal;

	@Override
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (!containsInterfaces(ModalEvent.class)) {
			Skript.error("The show modal effect can only be used in modal events.");
			return false;
		}
		exprModal = (Expression<Modal.Builder>) expressions[0];
		return true;
	}

	@Override
	public void runEffect(Event e) {
		final Modal.Builder modal = parseSingle(exprModal, e, null);
		if (modal == null) {
			restart();
			return;
		}
		Utils.catchAction(((ModalEvent) e).replyModal(modal.build()), v -> {
			restart();
		}, ex -> {
			restart();
			DiSky.getErrorHandler().exception(e, ex);
		});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "show modal " + exprModal.toString(e, debug);
	}
}
