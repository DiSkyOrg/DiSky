package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Lock / Unlock Thread")
@Description("Lock or unlock a specific thread.")
@Examples({"lock event-threadchannel",
		"unlock thread channel with id \"000\""})
@Since("4.4.0")
@SeeAlso(ThreadChannel.class)
public class LockUnlockThread extends AsyncEffect {

	static {
		Skript.registerEffect(
				LockUnlockThread.class,
				"lock [the] [thread] %threadchannel%",
				"unlock [the] [thread] %threadchannel%"
		);
	}

	private Expression<ThreadChannel> exprThread;
	private boolean lock;

	@Override
	public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);

		exprThread = expressions[0];
		lock = i == 0;
		return true;
	}

	@Override
	public void execute(@NotNull Event e) {
		final ThreadChannel thread = parseSingle(exprThread, e);
		if (thread == null)
			return;

		final ThreadChannelManager manager = thread.getManager();
		try {
			manager.setLocked(lock).complete();
		} catch (Exception ex) {
			DiSkyRuntimeHandler.error((Exception) ex);
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return (lock ? "lock" : "unlock") + " the thread " + exprThread.toString(e, debug);
	}
}
