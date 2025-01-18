package net.itsthesky.disky.elements.components.properties;

import ch.njol.skript.classes.Changer;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PropDefaultValues extends MultiplyPropertyExpression<Object, Object> {

	static {
		register(
				PropDefaultValues.class,
				Object.class,
				"[dropdown] default values",
				"dropdown"
		);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (EasyElement.equalAny(mode, Changer.ChangeMode.ADD))
			return new Class[] {User.class, Role.class, GuildChannel.class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] rawValues, @NotNull Changer.ChangeMode mode) {
		if (!mode.equals(Changer.ChangeMode.ADD))
			return;
		final Object entity = getExpr().getSingle(e);
		if (entity == null)
			return;

		if (entity instanceof EntitySelectMenu.Builder) {
			final EntitySelectMenu.Builder builder = (EntitySelectMenu.Builder) entity;

			final List<EntitySelectMenu.DefaultValue> values = new ArrayList<>();
			for (Object value : rawValues) {
				if (value instanceof User)
					values.add(EntitySelectMenu.DefaultValue.from((User) value));
				else if (value instanceof Role)
					values.add(EntitySelectMenu.DefaultValue.from((Role) value));
				else if (value instanceof GuildChannel)
					values.add(EntitySelectMenu.DefaultValue.from((GuildChannel) value));
			}

			builder.setDefaultValues(values);
		}
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "dropdown default values";
	}

	@Override
	public @Nullable Object[] convert(Object entity) {
		return new Object[0];
	}

	@Override
	public @NotNull Class<?> getReturnType() {
		return Object.class;
	}
}
