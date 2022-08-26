package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Lock / Unlock Thread")
@Description("Lock or unlock a specific thread.")
@Examples({"lock event-threadchannel",
		"unlock thread channel with id \"000\""})
@Since("4.4.0")
public class LockUnlockThread extends SpecificBotEffect {

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
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		exprThread = expressions[0];
		lock = i == 0;
		return true;
	}

	@Override
	public void runEffect(@NotNull Event e, @NotNull Bot bot) {
		final ThreadChannel thread = parseSingle(exprThread, e);
		if (thread == null) return;
		final ThreadChannelManager manager = thread.getManager();
		manager.setLocked(lock).queue();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return (lock ? "lock" : "unlock") + " the thread " + exprThread.toString(e, debug);
	}
}
