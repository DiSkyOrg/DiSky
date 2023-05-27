package info.itsthesky.disky.elements.properties.attachments;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;

import java.util.Arrays;

public class CondIsAudio extends PropertyCondition<Message.Attachment> {

	static {
		register(
				CondIsAudio.class,
				PropertyType.BE,
				"[an] audio",
				"attachment"
		);
	}

	@Override
	public boolean check(Message.Attachment attachment) {
		return attachment.getDuration() > 0;
	}

	@Override
	protected String getPropertyName() {
		return "attachment is audio";
	}

}