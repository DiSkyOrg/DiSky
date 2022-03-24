package info.itsthesky.disky.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.events.specific.MessageEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message is From Guild")
@Description({"Check either a message(related event come from a guild or from private messages.",
"This condition work with every event where a message is sent / received."})
@Examples({"if event is from guild:",
"if message come from private message:"})
public class CondMessageOrigin extends Condition {

	static {
		Skript.registerCondition(
				CondMessageOrigin.class,
				"[the] (message|event) (is coming|come from|is from) guild [channel]",
				"[the] (message|event) (is coming|come from|is from) (dm|(private|direct) message) [channel]"
		);
	}

	private boolean checkFromGuild;

	@Override
	public boolean check(@NotNull Event e) {
		final GenericMessageEvent event = ((MessageEvent) e).getMessageEvent();
		return checkFromGuild == event.isFromGuild();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "the message come from " + (checkFromGuild ? "guild" : "private message");
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		checkFromGuild = matchedPattern == 0;
		if (EasyElement.containsInterfaces(MessageEvent.class))
			return true;
		Skript.error("The 'message origin' condition can only be used in a message receive event.");
		return false;
	}
}
