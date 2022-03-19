package info.itsthesky.disky.elements.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("User / Guild Avatar")
@Description("Return the avatar URL of any user of guild.")
@Examples({"avatar of event-guild", "avatar of event-user"})
public class AvatarURL extends SimplePropertyExpression<Object, String> {

	static {
		register(
				AvatarURL.class,
				String.class,
				"avatar [url]",
				"guild/user"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "avatar";
	}

	@Override
	public @Nullable String convert(Object entity) {
		if (entity instanceof Guild)
			return ((Guild) entity).getIconUrl();
		if (entity instanceof User)
			return ((User) entity).getAvatarUrl();
		return null;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}
}
