package info.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EmbedProperty<T> extends SimplePropertyExpression<EmbedBuilder, T> {

	public static <T> void register(Class<? extends EmbedProperty<T>> clazz,
									Class<T> entityClass,
									String propertyName) {
		register(clazz, entityClass, propertyName, "embedbuilder");
	}

	private boolean useScope;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	protected abstract T convert(MessageEmbed embed);

	@Override
	public void change(@NotNull Event e, @Nullable Object[] delta, @NotNull Changer.ChangeMode mode) {
		final EmbedBuilder builder = EasyElement.parseSingle(getExpr(), e, null);
		if (builder == null)
			return;
		if (delta == null || delta[0] == null)
			return;
		final T entity = (T) delta[0];
		set(builder, entity);
	}

	public abstract void set(EmbedBuilder builder, T value);

	@Override
	public @Nullable T convert(EmbedBuilder builder) {
		if (builder.isEmpty()) {
			Skript.error("You are trying to get a property of an empty embed builder. This is not possible.");
			return null;
		}
		return convert(builder.build());
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "embed property";
	}
}
