package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Archive / Unarchive Thread")
@Description("Archive or unarchive a specific thread.")
@Examples({"archive event-threadchannel",
		"unarchive thread channel with id \"000\""})
@Since("4.4.0")
public class ArchiveUnarchiveThread extends AsyncEffect {

	static {
		Skript.registerEffect(
				ArchiveUnarchiveThread.class,
				"archive [the] [thread] %threadchannel%",
				"unarchive [the] [thread] %threadchannel%"
		);
	}

	private Expression<ThreadChannel> exprThread;
	private boolean archived;

	@Override
	public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);

		exprThread = (Expression<ThreadChannel>) expressions[0];
		archived = i == 0;

		return true;
	}

	@Override
	public void execute(@NotNull Event e) {
		final ThreadChannel thread = exprThread.getSingle(e);
		if (thread == null)
			return;

		final ThreadChannelManager manager = thread.getManager();

		try {
			manager.setArchived(archived).complete();
		} catch (Exception ex) {
			DiSky.getErrorHandler().exception(e, ex);
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return (archived ? "archive" : "unarchive") + " the thread " + exprThread.toString(e, debug);
	}
}
