package info.itsthesky.disky.elements.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Discord ID")
@Description({"Get the unique long value (ID) that represent a discord entity."})
@Examples({"discord id of event-channel",
"discord id of event-guild"})
public class DiscordId extends SimplePropertyExpression<Object, String> {

	static {
		register(DiscordId.class,
				String.class,
				"discord id",
				"channel/role/user/member/message/dropdown/button/guild");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "discord id";
	}

	@Override
	public @Nullable String convert(Object entity) {
		if (entity instanceof ISnowflake)
			return ((ISnowflake) entity).getId();
		if (entity instanceof ActionComponent)
			return ((ActionComponent) entity).getId();
		return null;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}
}
