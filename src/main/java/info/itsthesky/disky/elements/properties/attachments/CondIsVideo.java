package info.itsthesky.disky.elements.properties.attachments;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;

@Name("Is Attachment Video")
@Description("See if a specific attachment is a video.")
@Since("1.7")
public class CondIsVideo extends Condition {

	static {
		Skript.registerCondition(CondIsVideo.class,
				"att[achment[s]] %attachment% is [a] (vdo|video)",
		"att[achment[s]] %attachment% (isn't|is not|wasn't|was not) [a] (vdo|video)");
	}

	private Expression<Message.Attachment> exprAtt;
	private int pattern;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		exprAtt = (Expression<Message.Attachment>) exprs[0];
		pattern = matchedPattern;
		return true;
	}

	@Override
	public boolean check(Event e) {
		Message.Attachment att = exprAtt.getSingle(e);
		if (att == null) return false;
		if (pattern == 0) {
			return att.isVideo();
		} else {
			return !att.isVideo();
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "attachment " + exprAtt +" is a video";
	}

}