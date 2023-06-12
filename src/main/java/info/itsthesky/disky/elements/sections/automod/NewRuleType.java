package info.itsthesky.disky.elements.sections.automod;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;
import net.dv8tion.jda.api.entities.automod.build.TriggerConfig;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NewRuleType extends SimpleExpression<AutoModRuleData> {

	static {
		Skript.registerExpression(
				NewRuleType.class,
				AutoModRuleData.class,
				ExpressionType.COMBINED,
				"message with [the] %filtertype% filter %objects% (with name|named) %string%"
		);
	}

	private Expression<FilterType> exprType;
	private Expression<Object> exprFilter;
	private Expression<String> exprName;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprType = (Expression<FilterType>) exprs[0];
		exprFilter = (Expression<Object>) exprs[1];
		exprName = (Expression<String>) exprs[2];
		return true;
	}

	@Override
	protected AutoModRuleData @NotNull [] get(@NotNull Event e) {
		final FilterType filterType = EasyElement.parseSingle(exprType, e);
		final Object[] filters = EasyElement.parseList(exprFilter, e, null);
		final String name = EasyElement.parseSingle(exprName, e);

		if (EasyElement.anyNull(this, filters, name, filterType))
			return new AutoModRuleData[0];

		if (filterType.equals(FilterType.MENTION) && filters.length != 0 && !(filters[0] instanceof Number)) {
			Skript.error("The filter type 'mention' need a number as filter!");
			return new AutoModRuleData[0];
		} else {
			for (Object filter : filters) {
				if (filter instanceof String)
					continue;
				Skript.error("The filter type '" + filterType.name() + "' need a text/string as filter!");
				return new AutoModRuleData[0];
			}
		}

		switch (filterType) {
			case MENTION:
				final Number number = (Number) filters[0];
				return new AutoModRuleData[] {AutoModRuleData.onMessage(name, TriggerConfig.mentionSpam(number.intValue()))};
			case KEYWORD:
				final String[] keywords = new String[filters.length];
				for (int i = 0; i < filters.length; i++)
					keywords[i] = (String) filters[i];
				return new AutoModRuleData[] {AutoModRuleData.onMessage(name, TriggerConfig.keywordFilter(keywords))};
			case PATTERN:
				final String[] patterns = new String[filters.length];
				for (int i = 0; i < filters.length; i++)
					patterns[i] = (String) filters[i];
				return new AutoModRuleData[] {AutoModRuleData.onMessage(name, TriggerConfig.patternFilter(patterns))};
		}

		return new AutoModRuleData[0];
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends AutoModRuleData> getReturnType() {
		return AutoModRuleData.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "message with " + exprType.toString(e, debug) + " filter " + exprFilter.toString(e, debug) + " with name " + exprName.toString(e, debug);
	}
}
