package net.itsthesky.disky.elements.events;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import net.itsthesky.disky.api.events.BukkitEvent;
import net.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiSkyErrorEvent extends SelfRegisteringSkriptEvent {

	static {
		Skript.registerEvent(
				"DiSky Error / Exception",
				DiSkyErrorEvent.class, BukkitDiSkyErrorEvent.class,
				"disky (error|exception)"
		).description("Fired when any DiSky error occur.",
				"Since DiSky exception are per-event only, this regroup every exception occurred in every events.");

		SkriptUtils.registerValue(BukkitDiSkyErrorEvent.class, String.class,
				event -> event.getErrorMessage());
	}

	@Override
	public void register(@NotNull Trigger t) {}

	@Override
	public void unregister(@NotNull Trigger t) {}

	@Override
	public void unregisterAll() {}

	@Override
	public boolean init(Literal<?> @NotNull [] args, int matchedPattern, @NotNull ParseResult parseResult) {
		return true;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "on disky exception";
	}

	public static class BukkitDiSkyErrorEvent extends BukkitEvent {

		private final Throwable error;
		private final String eventName;

		public BukkitDiSkyErrorEvent(Throwable error, String eventName) {
			super(false);
			this.error = error;
			this.eventName = eventName;
		}

		public Throwable getError() {
			return error;
		}

		public String getErrorMessage() {
			return getError() == null ? "unknown error" : getError().getMessage();
		}

		@NotNull
		@Override
		public String getEventName() {
			return eventName;
		}
	}
}
