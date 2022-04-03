package info.itsthesky.disky.elements.properties.members;

import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public abstract class MultipleMemberProperty<T> extends MultiplyPropertyExpression<Member, T> {

	public static <T> void register(Class<? extends MultipleMemberProperty<T>> clazz,
									Class<T> entityClass,
									String propertyName) {
		register(clazz, entityClass, "[member] "+ propertyName, "member");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "member properties";
	}
}
