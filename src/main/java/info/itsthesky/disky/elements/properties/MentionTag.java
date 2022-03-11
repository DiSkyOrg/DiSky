package info.itsthesky.disky.elements.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.IMentionable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Mention Tag")
@Description({"Get the mention name of the discord entity.",
"It will return the similar format that when you are doing @ (roles, users) or # (channels) followed by names."})
@Examples({"mention tag of event-channel",
"mention tag of channel with id \"000\""})
public class MentionTag extends SimplePropertyExpression<IMentionable, String> {

	static {
		register(MentionTag.class,
				String.class,
				"mention [tag]",
				"channel/role/user/member");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "mention tag";
	}

	@Override
	public @Nullable String convert(IMentionable mentionable) {
		return mentionable.getAsMention();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}
}
