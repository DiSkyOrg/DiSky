package net.itsthesky.disky.elements.components;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.events.specific.ModalEvent;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.modals.Modal;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.containsInterfaces;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

public class OpenModal extends AsyncEffect {

	static {
		Skript.registerEffect(
				OpenModal.class,
				"(show|enable) [the] [modal] %modal% [to [the] [event[( |-)]]user]"
		);
	}

	private Expression<Modal.Builder> exprModal;
	private Node node;

	@Override
	public boolean init(Expression @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
		if (!containsInterfaces(ModalEvent.class)) {
			Skript.error("The show modal effect can only be used in interactions outside of modal events.");
			return false;
		}
		node = getParser().getNode();
		exprModal = (Expression<Modal.Builder>) expressions[0];
		return true;
	}

	@Override
	protected void execute(@NotNull Event event) {
		final Modal.Builder modal = parseSingle(exprModal, event, null);
		if (modal == null)
			return;

		if (modal.getComponents().isEmpty() || modal.getComponents().size() > 5) {
			DiSkyRuntimeHandler.error(new IllegalArgumentException("A modal must have between 1 and 5 components!"), node);
			return;
		}

		final ModalEvent modalEvent = (ModalEvent) event;
		modalEvent.replyModal(modal.build()).complete();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "show modal " + exprModal.toString(e, debug);
	}
}
