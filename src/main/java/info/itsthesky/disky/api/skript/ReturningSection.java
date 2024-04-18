package info.itsthesky.disky.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * A section that return a value once it's entirely run.
 * @author ItsTheSky
 */
public abstract class ReturningSection<T> extends Section {

	public static <T, S extends ReturningSection<T>> void register(Class<S> sectionClass,
																   Class<T> returnType,
																   Class expression,
																   String... patterns) {
		for (int i = 0; i < patterns.length; i++)
			patterns[i] += " [and store (it|the result) in %-~objects%]";

		Skript.registerSection(sectionClass, patterns);
		Skript.registerExpression(expression, returnType, ExpressionType.SIMPLE, "[the] "+expression.getSimpleName()+" [builder]");
	}

	public abstract T createNewValue(@NotNull Event event);

	private T currentValue;
	private Variable<T> variable;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
		loadOptionalCode(sectionNode);
		variable = (Variable<T>) exprs[exprs.length - 1];
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(@NotNull Event e) {

		currentValue = createNewValue(e);
		if (variable != null)
			variable.change(e, new Object[] {currentValue}, Changer.ChangeMode.SET);

		return walk(e, true);
	}

	public T getCurrentValue() {
		return currentValue;
	}

	public abstract static class LastBuilderExpression<T, S extends ReturningSection<T>> extends SimpleExpression<T> {

		private S section;

		@Override
		public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
			section = getParser().getCurrentSection(getSectionClass());
			return getParser().isCurrentSection(getSectionClass());
		}

		@Override
		protected T @NotNull [] get(@NotNull Event e) {
			return (T[]) new Object[] {section.getCurrentValue()};
		}

		@Override
		public boolean isSingle() {
			return true;
		}

		public Class<S> getSectionClass() {
			return (Class<S>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[1];
		};

		@Override
		public @NotNull Class<? extends T> getReturnType() {
			return (Class<T>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0];
		}

		@Override
		public @NotNull String toString(@Nullable Event e, boolean debug) {
			return "the last builder value";
		}
	}
}
