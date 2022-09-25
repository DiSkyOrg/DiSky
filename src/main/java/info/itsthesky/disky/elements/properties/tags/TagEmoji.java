package info.itsthesky.disky.elements.properties.tags;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Tag Emoji")
@Description("Gets the emoji of a forum tag. Can be null if the tag has no emoji.")
@Since("4.4.4")
public class TagEmoji extends SimplePropertyExpression<ForumTag, Emote> {

	static {
		register(
				TagEmoji.class,
				Emote.class,
				"tag emo(te|ji)",
				"forumtag"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "tag emoji";
	}

	@Override
	public @Nullable Emote convert(ForumTag forumTag) {
		if (forumTag.getEmoji() == null)
			return null;
		return new Emote(forumTag.getEmoji());
	}

	@Override
	public @NotNull Class<? extends Emote> getReturnType() {
		return Emote.class;
	}
}
