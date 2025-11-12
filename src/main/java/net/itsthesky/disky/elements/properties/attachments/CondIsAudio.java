package net.itsthesky.disky.elements.properties.attachments;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.Message;

@Name("Is Attachment Audio")
@Description({"Check if the specified attachment is an audio file (aka a vocal message).",
        "If it is, you'll be able to use the 'duration of attachment' expression."})
@Examples({"loop attachments of event-message:",
        "\tif loop-value is audio:",
        "\t\treply with \"Audio duration: %duration of loop-value%\""})
@Since("4.12.0")
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