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

@Name("Is Attachment Image")
@Description("See if a specific attachment is an image.")
@Since("1.7")
public class CondIsImage extends Condition {

	static {
		Skript.registerCondition(CondIsImage.class,
				"att[achment[s]] %attachment% is [an] (image|img)",
		"att[achment[s]] %attachment% (isn't|is not|wasn't|was not) [an] (image|img)");
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
			return att.isImage();
		} else {
			return !att.isImage();
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "attachment " + exprAtt +" is an image";
	}

}