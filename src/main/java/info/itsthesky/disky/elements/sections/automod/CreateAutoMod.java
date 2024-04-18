package info.itsthesky.disky.elements.sections.automod;

import info.itsthesky.disky.api.skript.ReturningSection;
import info.itsthesky.disky.managers.wrappers.AutoModRuleBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateAutoMod extends ReturningSection<AutoModRuleBuilder> {

	public static class rule extends LastBuilderExpression<AutoModRuleBuilder, CreateAutoMod> { }

	static {
		register(
				CreateAutoMod.class,
				AutoModRuleBuilder.class,
				CreateAutoMod.rule.class,
				"(make|create) [a] [new] auto[( |-)]mod [rule]"
		);
	}

	@Override
	public AutoModRuleBuilder createNewValue(Event event) {
		return new AutoModRuleBuilder();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "create a new automod rule";
	}

}
