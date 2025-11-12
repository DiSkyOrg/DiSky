package net.itsthesky.disky.elements.properties.forums;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;

@Name("Tag Required")
@Description({"Check if a forum channel require a tag to be set when creating a new post.",
"Can be changed using the 'tag required' expression."})
@Examples({"if event-forumchannel is tag required:",
"set tag required of event-forumchannel to false"})
@Since("4.0.0")
public class CondTagRequired extends PropertyCondition<ForumChannel> {

	static {
		register(CondTagRequired.class, PropertyCondition.PropertyType.BE, "tag required", "forumchannel");
	}

	@Override
	public boolean check(ForumChannel forumChannel) {
		return forumChannel.isTagRequired();
	}

	@Override
	protected String getPropertyName() {
		return "tag required";
	}

}
