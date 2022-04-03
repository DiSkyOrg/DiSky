package info.itsthesky.disky.elements.properties.invites;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Invite;
import org.jetbrains.annotations.NotNull;

public abstract class InviteProperty<T> extends SimplePropertyExpression<Invite, T> {

	public static <T> void register(Class<? extends InviteProperty<T>> clazz,
									Class<T> entityClass,
									String propertyName) {
		register(clazz, entityClass, "invite "+ propertyName, "invite");
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "invite property";
	}
}
