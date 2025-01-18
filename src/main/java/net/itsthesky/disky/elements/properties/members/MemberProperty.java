package net.itsthesky.disky.elements.properties.members;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public abstract class MemberProperty<T> extends SimplePropertyExpression<Member, T> {

	public static <T> void register(Class<? extends MemberProperty<T>> clazz,
									Class<T> entityClass,
									String propertyName) {
		register(clazz, entityClass, "[member] "+ propertyName, "member");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "member property";
	}
}
