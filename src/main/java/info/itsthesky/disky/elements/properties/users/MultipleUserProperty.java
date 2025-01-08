package info.itsthesky.disky.elements.properties.users;

import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public abstract class MultipleUserProperty<T> extends MultiplyPropertyExpression<User, T> {

	public static <T> void register(Class<? extends MultipleUserProperty<T>> clazz,
									Class<T> entityClass,
									String propertyName) {
		register(clazz, entityClass, "[user] "+ propertyName, "user");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "user properties";
	}
}
