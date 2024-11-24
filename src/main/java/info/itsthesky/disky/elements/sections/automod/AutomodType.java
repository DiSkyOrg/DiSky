package info.itsthesky.disky.elements.sections.automod;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.managers.wrappers.AutoModRuleBuilder;
import net.dv8tion.jda.api.entities.automod.build.AutoModRuleData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutomodType extends SimplePropertyExpression<AutoModRuleBuilder, AutoModRuleData> {

	static {
		register(
				AutomodType.class,
				AutoModRuleData.class,
				"type",
				"automodrule"
		);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		if (!getParser().isCurrentSection(CreateAutoMod.class)) {
			Skript.error("You can only use the 'rule type' expression inside a 'create auto mod rule' section");
			return false;
		}
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET)
			return new Class[]{AutoModRuleData.class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final AutoModRuleData data = (AutoModRuleData) delta[0];
		final AutoModRuleBuilder builder = EasyElement.parseSingle(getExpr(), e, null);

		if (data == null || builder == null)
			return;

		builder.setAutoModRule(data);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "rule type";
	}

	@Override
	public @Nullable AutoModRuleData convert(AutoModRuleBuilder messageCreateBuilder) {
		return null;
	}

	@Override
	public @NotNull Class<? extends AutoModRuleData> getReturnType() {
		return AutoModRuleData.class;
	}
}
