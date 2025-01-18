package net.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Color;
import net.itsthesky.disky.api.changers.ChangeableSimplePropertyExpression;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Embed Color")
@Description({"Get or change the color of an embed builder.",
		"The color input must come from Skript, and will be converted by DiSky."})
@Examples({"set embed color of embed to red"})
public class ColorOf extends ChangeableSimplePropertyExpression<Object, Color>
		implements IAsyncChangeableExpression {

	static {
		register(ColorOf.class,
				Color.class,
				"(embed|discord) colo[u]r",
				"embedbuilder");
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, Bot bot, @NotNull Changer.ChangeMode mode) {
		change(e, delta, mode, false);
	}

	@Override
	public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
		change(e, delta, mode, true);
	}

	public void change(Event e, Object[] delta, Changer.ChangeMode mode, boolean async) {
		if (!EasyElement.isValid(delta))
			return;
		final Object entity = EasyElement.parseSingle(getExpr(), e, null);
		if (entity == null)
			return;

		@Nullable java.awt.Color target = null;
		if (mode == Changer.ChangeMode.SET) {
			final Color color = (Color) delta[0];
			target = new java.awt.Color(color.asBukkitColor().asRGB());
		}

		if (entity instanceof Role) {
			Role role = (Role) entity;


			var action = role.getManager().setColor(target);

			if (async) action.complete();
			else action.queue();
		} else if (entity instanceof EmbedBuilder) {
			((EmbedBuilder) entity).setColor(target);
		}
	}

	@Override
	public Class<?> @Nullable [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET)
			return new Class[] {Color.class};
		return null;
	}

	@Override
	public @NotNull Class<? extends Color> getReturnType() {
		return Color.class;
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "color";
	}

	@Override
	public @Nullable Color convert(Object entity) {
		if (entity instanceof EmbedBuilder)
			return ((EmbedBuilder) entity).isEmpty() ? null : SkriptUtils.convert(((EmbedBuilder) entity).build().getColor());
		if (entity instanceof Role)
			return SkriptUtils.convert(((Role) entity).getColor());
		return null;
	}
}
