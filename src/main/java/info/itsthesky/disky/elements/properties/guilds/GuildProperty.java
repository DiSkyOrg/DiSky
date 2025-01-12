package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;

public abstract class GuildProperty<T> extends SimplePropertyExpression<Guild, T> {

	public static <T> void register(Class<? extends GuildProperty<T>> clazz,
									Class<T> entityClass,
									String propertyName) {
		register(clazz, entityClass, "[guild] "+ propertyName, "guild");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "guild property";
	}

	public abstract T converting(Guild guild);

	@Override
	public T convert(Guild guild) {
		return converting(guild);
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull Class<? extends T> getReturnType() {
		return (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

}
