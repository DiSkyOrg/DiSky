package net.itsthesky.disky.elements.properties.users;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public abstract class UserProperty<T> extends SimplePropertyExpression<User, T> {

	public static <T> void register(Class<? extends UserProperty<T>> clazz,
									Class<T> entityClass,
									String propertyName) {
		register(clazz, entityClass, "[user] "+ propertyName, "user");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "user property";
	}
}
