package info.itsthesky.disky.elements.properties.forums;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Default Forum Emoji")
@Description({"Represent the default emoji of a forum channel.",
"It's the mote that is added automatically once a new post is created.",
"Can return none and can be changed."})
@Examples("set default emoji of event-forumchannel to reaction \"smile\"")
public class DefaultEmoji extends SimplePropertyExpression<ForumChannel, Emote> {

	static {
		register(DefaultEmoji.class, Emote.class,
				"default [forum] emoji",
				"forumchannel"
		);
	}

	@Override
	public @Nullable Emote convert(ForumChannel forumChannel) {
		if (forumChannel.getDefaultReaction() == null)
			return null;

		return new Emote(forumChannel.getDefaultReaction());
	}

	@Override
	public @NotNull Class<? extends Emote> getReturnType() {
		return Emote.class;
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "default forum emoji";
	}
}
